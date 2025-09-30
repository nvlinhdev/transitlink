package vn.edu.fpt.transitlink.shared.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.annotation.ElementType;

/**
 * Gắn vào Controller class hoặc handler method để bỏ qua cơ chế tự động
 * chèn các ErrorResponse chuẩn (400,401,403,404,409,422,500) trong tài liệu OpenAPI.
 * Không ảnh hưởng tới runtime exception handling.
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface SkipGlobalErrorResponses {
}