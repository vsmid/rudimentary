# Internationalization (i18n)

## Introduction
Application/services im most cases report some information to the end user about the action they took.
In order to standardize and simplify info reporting in multiple languages Rudimentary offers simple wrapper around Java's `ResourceBundle`.

## Configuration
Internationalization is configured via two configuration properties.
```properties
i18n.locale=en # Set default locale, if not set, default system locale is used
i18n.bundles=classpath:messages, /Users/messages # Comma separated list of resource bundle files
```

### Classpath resource
If resource file is placed in `src/main/resourcs` directory you must use prefix `classpath:`.

Example:
```
+ src/main/resources
    messages_en.properties
```

```properties
i18n.bundles=classpath:messages
```

#### Filesystem resource
If resource file is placed somwhere in filesystem directory you must use absolute file path without language
and .properties extension.

Example:
```
+ /Users/admin/messages
    validation_en.properties
```

```properties
i18n.bundles=/Users/admin/messages/validation
```

## Usage

### Get text with using default locale
```java
  I18n.text("message");
  I18n.text("message", "World"); // If message under message key contains parameter placeholder e.g. message=Hello {0}!
```

### Get text with using specific locale
```java
  I18n.text("message", Locale.GERMAN);
  I18n.text("message", Locale.GERMAN, "Welt"); // If message under message key contains parameter placeholder e.g. message=Hallo {0}!
```

### Get text for message key that is not yet set in properties file
Intended use for this is if you want to set default message and allow for it to be overriden by setting given property.
```java
  I18n.text("message.override", "Hello beautiful {0}", "World");
```
In the above case, if you put `message.override=Just Hello {0}` property to one of you configured resource bundles, when asking for `message.override`message you will get the value from that resource bundle.

## Default constraint messages keys
Rudimentary constraint messages can also be overriden.

Currently these are the messages you can override:
* constraint.notNull - parameters you can use in message format are: input value
* constraint.notEmpty - parameters you can use in message format are: input value
* constraint.min - parameters you can use in message format are: input value, min constraint value
* constraint.max - parameters you can use in message format are: input value, max constraint value
* constraint.minLength - parameters you can use in message format are: input value length, min length constraint value
* constraint.maxLength - parameters you can use in message format are: input value length, max length constraint value
* constraint.pattern - parameters you can use in message format are: input value, pattern constraint as string value
