# Pooling

## Introduction
Sometimes object creation can be expensive as in time consuming. That is why we need to have a pool of already prepared objects to avoid object creation on the fly. Rudimentary offers simple object pooling mechanism.

## Create you own object pool
Creating new object pool is done by extending `hr.yeti.rudimentary.pooling.spi.ObjectPool` class. You can have as many different object pool providers as you like and you can register them in a `src/main/resources/META-INF/services/hr.yeti.rudimentary.pooling.spi.ObjectPool` file. This however, rudimentary-maven-plugin already automatically does for you.
```java
public class CustomObjectPool extends ObjectPool<String> {

  @Override
  protected String createObject() throws ObjectPoolException {
      // Implement how object is created
      return new String("Expensive object");
  }

  @Override
  protected ObjectPoolSettings settings() {
      // Implement object pool configuration settings.
      return return new ObjectPoolSettings(5, 10, 30, 15); // These are hard-coded values, you can use Rudimantary config for configurable values
  }

}
```
### ObjectPoolSettings
* minSize - Set the minimal size of the pool.
* maxSize - Set the maximal size of the pool.
* validationInterval - Period of time in seconds between two pool state validation checks. During validation
checks, a pool is populated with new instances if the current number of instances in the pool is lower than
minSize or instances are removed from the pool if the current number of instances in the pool is greater than
maxSize. For more technical details on validationInterval check implementation of `ObjectPool#initialize`.
* awaitTerminationInterval - Period of time in seconds object pool will wait before it terminates itself.

## Get object from object pool
To get object from object pool use `ObjectPool#borrow` method.

## Return object to object pool
After you are done with used object you need to return it to the pool. To return used object to the object pool use `ObjectPool#release` method.
