package vn.edu.fpt.transitlink.shared.config;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import io.swagger.v3.core.converter.AnnotatedType;
import io.swagger.v3.core.converter.ModelConverter;
import io.swagger.v3.core.converter.ModelConverterContext;
import io.swagger.v3.oas.models.media.ArraySchema;
import io.swagger.v3.oas.models.media.MapSchema;
import io.swagger.v3.oas.models.media.Schema;
import vn.edu.fpt.transitlink.shared.dto.StandardResponse;

import java.util.Iterator;

public class StandardResponseModelConverter implements ModelConverter {

    @Override
    public Schema<?> resolve(AnnotatedType type, ModelConverterContext context, Iterator<ModelConverter> chain) {
        if (type == null || type.getType() == null) {
            return null;
        }

        TypeFactory tf = TypeFactory.defaultInstance();
        JavaType javaType = tf.constructType(type.getType());

        // Nếu là StandardResponse<T>
        if (javaType.hasRawClass(StandardResponse.class) && javaType.getBindings().size() == 1) {
            JavaType dataType = javaType.getBindings().getBoundType(0);

            Schema<?> wrapperSchema = new Schema<>()
                    .type("object")
                    .addProperty("success", new Schema<>().type("boolean"))
                    .addProperty("message", new Schema<>().type("string"))
                    .addProperty("timestamp", new Schema<>().type("string").format("date-time"));

            String suffix;

            if (dataType.getRawClass().equals(Void.class)) {
                // Không thêm "data" property
                suffix = "Void";

            } else if (dataType.isCollectionLikeType()) {
                // List<T> / Set<T>
                JavaType contentType = dataType.getContentType();
                Schema<?> itemSchema = context.resolve(new AnnotatedType(contentType).resolveAsRef(false));
                Schema<?> dataSchema = new ArraySchema().items(itemSchema);

                wrapperSchema.addProperty("data", dataSchema);
                suffix = contentType.getRawClass().getSimpleName() + "List";

            } else if (dataType.isMapLikeType()) {
                // Map<String, T>
                JavaType valueType = dataType.getContentType();
                Schema<?> valueSchema = context.resolve(new AnnotatedType(valueType).resolveAsRef(false));
                Schema<?> dataSchema = new MapSchema().additionalProperties(valueSchema);

                wrapperSchema.addProperty("data", dataSchema);
                suffix = "Map" + valueType.getRawClass().getSimpleName();

            } else {
                // Kiểu đơn giản: UserDTO, String, Integer...
                Schema<?> dataSchema = context.resolve(new AnnotatedType(dataType).resolveAsRef(false));
                wrapperSchema.addProperty("data", dataSchema);
                suffix = dataType.getRawClass().getSimpleName();
            }

            wrapperSchema.setName("StandardResponse" + suffix);
            return wrapperSchema;
        }

        // Chuyển tiếp cho converter khác nếu có
        if (chain.hasNext()) {
            return chain.next().resolve(type, context, chain);
        }
        return null;
    }
}
