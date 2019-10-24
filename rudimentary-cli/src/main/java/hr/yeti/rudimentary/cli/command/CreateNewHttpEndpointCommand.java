package hr.yeti.rudimentary.cli.command;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Map;
import java.util.Objects;

import hr.yeti.rudimentary.cli.Command;

public class CreateNewHttpEndpointCommand implements Command {

  @Override
  public String name() {
    return "new-http-endpoint";
  }

  @Override
  public String shorthand() {
    return "nhe";
  }

  @Override
  public String description() {
    return "Creates new http endpoint.";
  }

  @Override
  public Map<String, String> options() {
    return Map.of("package", "Location where to create new endpoint class.", "className",
        "The name of new http endpoint class.", "request-body-type",
        "Allowed values are: Empty, Text, Json, ByteStream, Html, Form. Defaults to Empty.", "response-type",
        "Allowed values are: Empty, Text, Json, ByteStream, Html, Redirect, View. Defaults to Empty.", "http-method",
        "POST, GET, DELETE, PUT etc. Defaults to GET.", "path",
        "Http endpoint path(uri). Defaults to class name with lowercase starting letter.");
  }

  @Override
  public void execute(Map<String, String> arguments) {
    String packageName = null;
    String className = null;

    if (!arguments.containsKey("package")) {
      System.err.println("Please set --package value.");
    } else {
      packageName = arguments.get("package");
    }

    if (!arguments.containsKey("className")) {
      System.err.println("Please set --className value.");
    } else {
      className = arguments.get("className");
    }

    if(Objects.isNull(packageName) || Objects.isNull(className)) {
      System.exit(-1);
    }

    try {
      Path newFilePath = Path.of(new File("").toPath().toAbsolutePath().toString(), "src/main/java",
          packageName.replaceAll("\\.", "/"));

      if (!Files.isDirectory(newFilePath)) {
        Files.createDirectories(newFilePath);
      }

      Files.write(newFilePath.resolve(className + ".java"),
          httpEndpointTemplate(arguments.get("http-method"), arguments.get("path"), packageName, className,
              arguments.getOrDefault("request-body-type", "Empty"), arguments.getOrDefault("response-type", "Empty"))
                  .getBytes(),
          StandardOpenOption.CREATE_NEW);
    } catch (IOException ex) {
      ex.printStackTrace();
    }

  }

  private String httpEndpointTemplate(String httpMethod, String path, String packageName, String className,
      String requestBodyType, String responseBodyType) {

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
