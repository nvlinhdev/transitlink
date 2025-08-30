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

import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;
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

            // Tạo example timestamp cho hiện tại
            String exampleTimestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss 'UTC'"));

            Schema<?> wrapperSchema = new Schema<>()
                    .type("object")
                    .addProperty("success", new Schema<>().type("boolean").example(true))
                    .addProperty("message", new Schema<>().type("string").example("Operation completed successfully"))
                    .addProperty("timestamp", new Schema<>().type("string").format("date-time").example(exampleTimestamp));

            String suffix;

            if (dataType.getRawClass().equals(Void.class)) {
                // Không thêm "data" property
                suffix = "Void";

            } else if (dataType.isCollectionLikeType()) {
                // List<T> / Set<T>
                JavaType contentType = dataType.getContentType();
                Schema<?> itemSchema = context.resolve(new AnnotatedType(contentType).resolveAsRef(false));
                Schema<?> dataSchema = new ArraySchema().items(itemSchema);

                // Thêm mẫu ví dụ cho mảng rỗng
                dataSchema.example("[]");

                wrapperSchema.addProperty("data", dataSchema);
                suffix = contentType.getRawClass().getSimpleName() + "List";

            } else if (dataType.isMapLikeType()) {
                // Map<String, T>
                JavaType valueType = dataType.getContentType();
                Schema<?> valueSchema = context.resolve(new AnnotatedType(valueType).resolveAsRef(false));
                Schema<?> dataSchema = new MapSchema().additionalProperties(valueSchema);

                // Thêm mẫu ví dụ cho map rỗng
                dataSchema.example("{}");

                wrapperSchema.addProperty("data", dataSchema);
                suffix = "Map" + valueType.getRawClass().getSimpleName();

            } else {
                // Kiểu đơn giản: UserDTO, String, Integer...
                Schema<?> dataSchema = context.resolve(new AnnotatedType(dataType).resolveAsRef(false));

                // Thêm example cơ bản cho các kiểu dữ liệu phổ biến
                if (dataType.getRawClass() == String.class) {
                    dataSchema.example("Example string value");
                } else if (dataType.getRawClass() == Integer.class || dataType.getRawClass() == int.class) {
                    dataSchema.example(123);
                } else if (dataType.getRawClass() == Long.class || dataType.getRawClass() == long.class) {
                    dataSchema.example(123456789L);
                } else if (dataType.getRawClass() == Boolean.class || dataType.getRawClass() == boolean.class) {
                    dataSchema.example(true);
                } else if (dataType.getRawClass() == Double.class || dataType.getRawClass() == double.class) {
                    dataSchema.example(123.45);
                } else if (dataType.getRawClass() == Float.class || dataType.getRawClass() == float.class) {
                    dataSchema.example(123.45f);
                } else if (dataType.getRawClass().isEnum()) {
                    // Lấy giá trị đầu tiên của enum nếu có
                    try {
                        Object[] enumConstants = dataType.getRawClass().getEnumConstants();
                        if (enumConstants != null && enumConstants.length > 0) {
                            dataSchema.example(enumConstants[0].toString());
                        }
                    } catch (Exception ignored) {
                        // Ignore if can't get enum values
                    }
                } else if (dataType.getRawClass() == java.util.UUID.class) {
                    dataSchema.example("a573aa20-f56b-4888-8b5b-88a7ad21b928");
                }

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
