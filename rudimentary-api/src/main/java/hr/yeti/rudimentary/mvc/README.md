# MVC
Rudimentary offers simple way to render web page templates in Model-View-Controller fashion. It is also very easy to add your own templating engine.

## ViewEndpoint
`hr.yeti.rudimentary.mvc.spi.ViewEndpoint` is a special kind of `hr.yeti.rudimentary.http.spi.HttpEndpoint` which you should use if you want to render some web page template in browser. This interface always defines response as a `hr.yeti.rudimentary.http.content.View` content type.

## Default view rendering engine
Default view rendering engine is set to `DefaultStaticHTMLViewEngine` which only loads and renders static HTML and is available out-of-the box. Use this if you do not need any dynamic rendering features.

## Adding view rendering engine
To use custom view rendering engine you must add Rudimentary extension to your project's `pom.xml`. For now, there is only [Pebble](https://pebbletemplates.io/) extension.
```xml
<dependency>
  <groupId>hr.yeti.rudimentary.exts</groupId>
  <artifactId>rudimentary-mvc-pebble-ext</artifactId>
  <version>1.0-SNAPSHOT</version>
</dependency> 
```

## Configuring MVC
There are two configuration properties you can customize.
```properties
mvc.templatesDir=view # Directory where templates are stored under src/main/resources. Defaults to view.
mvc.staticResourcesDir=static # Directory where static resources such as css, javascript etc. are stored under src/main/resources. Defaults to static.
```

## Creating new ViewEndpoint
Creating new ViewEndpoint is as simple as implementing `hr.yeti.rudimentary.mvc.spi.ViewEndpoint` interface.
Below shown example is using `Pebble` as view rendering engine.

`Project layout`
```
  ...
  + src/main/resources
    + static
        js.js
        style.css
    + view
        dynamicView.html
```
`dynamicView.html`
```html
<html>
    <head>
        <title>Dynamic view</title>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <link href="../static/style.css" type="text/css" rel="stylesheet"/>
    </head>
    <body>
        <div>Your name is: {{name}}.</div>
        <div>You are: {{age}} years old.</div>
        <button onclick="greet('{{name}}')">Greet</button>
        <script src="../static/js.js"></script>
    </body>
</html>
```
`CustomViewEndpoint.java`
```java
public class CustomViewEndpoint implements ViewEndpoint<Form> {

  @Override
  public String path() {
      return "/dynamicView";
  }

  @Override
  public View response(Request<Form> request) {
      return new View("dynamicView.html", request.getBody().getValue()); // request.getBody().getValue() is already an instance of java.util.Map
  }

}
```

## Registering new ViewEndpoint
You can have as many different ViewEndpoint providers as you want and you can register them in `src/main/resources/META-INF/services/hr.yeti.rudimentary.http.spi.ViewEndpoint` file like any pther Java service provider. This however, `rudimentray-maven-plugin` already does automatically for you.

## Creating custom template engine
If you would like to use some other template engine other than `Pebble` you can easily create your own extension.
It is as easy as implementing `hr.yeti.rudimentary.mvc.spi.ViewEngine` and registering it in `src/main/resources/META-INF/services/hr.yeti.rudimentary.mvc.spi.ViewEngine` file like any other Java service provider (no automatic provider registration available). Only one `ViewEngine` should be registered per project.
It is best to ship this file along with `.jar` containing extension implementation. See [Pebble extension](https://github.com/vsmid/rudimentary/tree/master/rudimentary-exts/rudimentary-mvc-pebble-ext) as an example.
Now to use your custom extension just add Maven coordinates of your artifact to your project's `pom.xml` file.

