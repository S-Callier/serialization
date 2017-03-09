# serialization

This library is small tool designed to help handle basic serialization while taking advantage of java 8 possibilities.

The self imposed constraints are:
* No dependencies because Jar Hell is evil.
* No use of Unsafe.

## Getting started

The first step is to create you own CodecCache that will contain all the codecs you want to use. 

Codecs are generated and usable on a class by class basis.
Once an object instance is serialized, the first byte (marker) will be used to keep track of the codec that was used and the remaining will be the actual field values of this instance.

The CodecCache is used to register all the codecs used, and since the byte markers are important, the order of insertion of the codecs in the cache will usually be important.

```java
CodecCache cache = new CodecCache();

//The next free marker of this cache will be used.
//Many of the provided codecs are using several consecutive bytes as marker.
//As soon as one marker is manually assigned, it is preferable to manually assign them all
LongCodec longCodec = new LongCodec(cache.nextFreeMarker());
//Actually add the codec to the cache
cache.register(longCodec);

//Add the other codecs you will need
MapCodec mapCodec = new MapCodec(cache.nextFreeMarker(), cache);
cache.register(mapCodec);
```

One byte is reserved for null values, leaving up to 255 codecs to be used.

Once the codec cache contains all the required codecs, it can be used to serialize / deserialize

```java
SerializerFactory serializerFactory = new SerializerFactory(cache);
DeserializerFactory deserializerFactory = new DeserializerFactory(cache);
```

For the serializers (resp. deserializers) the method write (resp. read) as an optional codec argument.
If the codec is provided, it will be used as is, otherwise the right codec will be found in the cache (which is slightly slower).

## Adding extra codecs 