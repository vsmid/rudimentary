## Http

### Introduction
`hr.yeti.rudimentary.http.spi.HttpEndpoint` is probably the most important SPI for any Rudimentary developer. This is the class to implement to expose some functionality over http.  

### Idea & design
This SPI is designed in such a way that it only allows one http method implementation per class. This is quite different to what we see in most of today's modern frameworks. This makes code more readable and testable. Also, since Rudimentary uses Java  module system it is super easy to include any number of modules consisting of just one HttpEndpoint implementation to the Rudimentary runtime.

### HttpEndpoint

