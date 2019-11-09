## Configuration

### Introduction
Makes dealing with application/service configuration a little bit easier. 
A set of loaders is provided to enable configuration loading from basically any source.
Also, a convenient set of property value converters is available to make value transformation a breeze.

### Configuration properties loading hierarchy
Property value is resolved based on the following order(top to bottom, top has the highest priority):

* System property
* Environment property
* Any kind of properties loading mechanism using *Config#load* methods
* Default value provided on the fly `e.g. new ConfigProperty("val", "defaultValue")`

### Configuration provider

#### Default provider

#### Registering provider

#### Custom provider

### Transformer methods

### Examples