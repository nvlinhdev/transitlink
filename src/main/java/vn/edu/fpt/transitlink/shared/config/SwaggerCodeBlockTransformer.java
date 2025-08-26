package vn.edu.fpt.transitlink.shared.config;

import jakarta.servlet.http.HttpServletRequest;
import org.springdoc.core.properties.SwaggerUiConfigProperties;
import org.springdoc.core.properties.SwaggerUiOAuthProperties;
import org.springdoc.core.providers.ObjectMapperProvider;
import org.springdoc.webmvc.ui.SwaggerIndexPageTransformer;
import org.springdoc.webmvc.ui.SwaggerWelcomeCommon;
import org.springframework.core.io.Resource;
import org.springframework.web.servlet.resource.ResourceTransformerChain;
import org.springframework.web.servlet.resource.TransformedResource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

public class SwaggerCodeBlockTransformer extends SwaggerIndexPageTransformer {

    public SwaggerCodeBlockTransformer(SwaggerUiConfigProperties a,
                                       SwaggerUiOAuthProperties b,
                                       SwaggerWelcomeCommon c,
                                       ObjectMapperProvider d) {
        super(a, b, c, d);
    }

    @Override
    public Resource transform(HttpServletRequest request,
                              Resource resource,
                              ResourceTransformerChain transformer) throws IOException {

        if ("index.html".equals(resource.getFilename())) {
            try (InputStream is = resource.getInputStream();
                 BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {

                String html = br.lines().collect(Collectors.joining("\n"));

                String customScript = """
                        <script>
                          (function() {
                            function applyToken() {
                              const token = localStorage.getItem("access_token");
                              if (token && window.ui) {
                                try {
                                  window.ui.preauthorizeApiKey("bearer-jwt", token);
                                  console.log("✅ Token applied to Swagger UI");
                                  return true;
                                } catch (e) {
                                  console.warn("⚠️ Không apply được token", e);
                                }
                              } else if (!token && window.ui) {
                                // clear token bằng authorize rỗng
                                try {
                                  if (window.ui.getSystem && window.ui.getSystem().authActions) {
                                    window.ui.getSystem().authActions.authorize({
                                      "bearer-jwt": {
                                        name: "bearer-jwt",
                                        schema: { type: "apiKey", in: "header", name: "Authorization" },
                                        value: ""
                                      }
                                    });
                                    console.log("🚪 Token cleared from Swagger UI");
                                  }
                                } catch(e) {
                                  console.warn("Không clear được auth", e);
                                }
                              }
                              return false;
                            }
                        
                            // Khi Swagger UI load xong thì apply ngay token hiện tại
                            window.addEventListener("load", function() {
                              let attempts = 0;
                              const interval = setInterval(() => {
                                attempts++;
                                if (applyToken() || attempts > 10) {
                                  clearInterval(interval);
                                }
                              }, 300);
                            });
                        
                            // Lắng nghe thay đổi storage (ví dụ khi logout/login ở auth-dash)
                            window.addEventListener("storage", function(e) {
                              if (e.key === "access_token") {
                                applyToken();
                              }
                            });
                          })();
                        </script>
                        """;

                // Inject ngay trước </body>
                String transformed = html.replace("</body>", customScript + "\n</body>");

                return new TransformedResource(resource, transformed.getBytes(StandardCharsets.UTF_8));
            }
        }
        return super.transform(request, resource, transformer);
    }

}

