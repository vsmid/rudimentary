package hr.yeti.rudimentary.autoregister.maven.plugin.mojo;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Objects;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

/**
 * Creates new http endpoint.
 *
 * @author vedransmid@yeti-it.hr
 */
@Mojo(name = "new-endpoint")
public class CreateNewHttpEndpointMojo extends AbstractMojo {

    /**
     * Location where to create new endpoint class.
     */
    @Parameter(property = "package")
    String pckg;

    /**
     * The name of new http endpoint class.
     */
    @Parameter(property = "className")
    String className;

    /**
     * Http endpoint path(uri). Defaults to class name with lowercase starting letter.
     */
    @Parameter(property = "path")
    String path;

    /**
     * POST, GET, DELETE, PUT etc. Defaults to GET.
     */
    @Parameter(property = "httpMethod")
    String httpMethod;

    /**
     * Allowed values are: Empty, Text, Json, ByteStream, Html, Form. Defaults to Empty.
     */
    @Parameter(property = "requestBodyType", defaultValue = "Empty")
    String requestBodyType;

    /**
     * Allowed values are: Empty, Text, Json, ByteStream, Html, Redirect, View. Defaults to Empty.
     */
    @Parameter(property = "responseType", defaultValue = "Empty")
    String responseType;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {

        if (Objects.isNull(pckg)) {
            System.err.println("Please set -Dpackage value.");
        }

        if (Objects.isNull(className)) {
            System.err.println("Please set -DclassName value.");
        }

        if (Objects.isNull(pckg) || Objects.isNull(className)) {
            System.exit(-1);
        }

        try {
            Path newFilePath = Path.of(new File("").toPath().toAbsolutePath().toString(), "src/main/java",
                pckg.replaceAll("\\.", "/"));

            if (!Files.isDirectory(newFilePath)) {
                Files.createDirectories(newFilePath);
            }

            Files.write(newFilePath.resolve(className + ".java"),
                httpEndpointTemplate(httpMethod, path, pckg, className,
                    requestBodyType, responseType
                )
                    .getBytes(),
                StandardOpenOption.CREATE_NEW
            );
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private String httpEndpointTemplate(String httpMethod, String path, String packageName, String className, String requestBodyType, String responseBodyType) {
        // Http method
        String httpMethodMethod = httpMethodTemplate(httpMethod);

        String httpMethodImport = httpMethodMethod.length() == 0 ? "" : "import hr.yeti.rudimentary.http.HttpMethod;\n";
        String contentTypeImport = "import hr.yeti.rudimentary.http.content." + requestBodyType + ";\n";
        if (!requestBodyType.equals(responseBodyType)) {
            contentTypeImport += "import hr.yeti.rudimentary.http.content." + responseBodyType + ";\n";
        }

        // Path
        String pathMethod = pathMethodTemplate(path);
        String pathMethodImport = pathMethod.length() == 0 ? "" : "import java.net.URI;\n";

        return String.format(
            "package %s;\n" + "\n" + "%s" + "import hr.yeti.rudimentary.http.spi.HttpEndpoint;\n"
            + "import hr.yeti.rudimentary.http.Request;\n" + "%s" + "%s" + "\n"
            + "public class %s implements HttpEndpoint<%s, %s>{\n" + "%s" + "%s" + "\n" + "  @Override\n"
            + "  public %s response(Request<%s> request) {\n" + "    return null;\n" + "  }\n" + "\n" + "}\n" + "",
            packageName, httpMethodImport, contentTypeImport, pathMethodImport, className, requestBodyType,
            responseBodyType, httpMethodMethod, pathMethod, responseBodyType, requestBodyType);
    }

    private String httpMethodTemplate(String httpMethod) {
        if (Objects.isNull(httpMethod)) {
            return "";
        }

        return "\n" + "  @Override\n" + "  public HttpMethod httpMethod() {\n" + "    return HttpMethod." + httpMethod
            + ";\n" + "  }\n";
    }

    private String pathMethodTemplate(String path) {
        if (Objects.isNull(path)) {
            return "";
        }

        return "\n" + "  @Override\n" + "  public URI path() {\n" + "    return URI.create(\"" + path + "\");\n" + "  }\n";
    }
}
