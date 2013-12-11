# Lightstreamer - Reusable Metadata Adapters - Java SE Adapter #

This project includes two simple full implementations of Metadata Adapter in Java made available as sample for inspiration and/or extension.

## LiteralBasedProvider Metadata Adapter ##

The LiteralBasedProvider extends the MetadataProviderAdapter abstract class (which in turn implements the MetadataProvider interface). It is used in many Lightstreamer examples and demos, in combination with different Data Adapters and Clients.
It is also useful when developing proof of concepts and prototypes where the main focus is on the Data Adapter.

## FileBasedProvider Metadata Adapter ##

The FileBasedProvider extends the LiteralBasedProvider, but is rarely used.

# Build #

Before you can compile the adapters in the jar some dependencies need to be solved:
* Get the ls-adapter-interface.jar file from the [Lightstreamer 5 Colosseo distribution](http://www.lightstreamer.com/download).

Now you can generate the ls-adapter.interface.jar with the following commands:
```sh
  >javac -source 1.7 -target 1.7 -nowarn -g -classpath compile_libs/ls-adapter-interface.jar -sourcepath src -d tmp_classes src/com/lightstreamer/adapters/metadata/LiteralBasedProvider.java

  >jar cvf ls-adapter-interface.jar -C tmp_classes src
```

# Deploy #

To use these Metadata Adapters, just copy the ls-generic-adapters.jar file to the "shared/lib" directory of your Lightstreamer Server installation (usually that jar file comes pre-installed). 
Then configure the right Metadata provider and its properties in the "adapters.xml" descriptor file within your adapters' subfolder. The following code snippet shows an example: 
```xml
  <!-- Mandatory. Define the Metadata Provider. -->
  <metadata_provider>
    <!-- Mandatory. Java class name of the adapter. -->
    <adapter_class>com.lightstreamer.adapters.metadata.LiteralBasedProvider</adapter_class>

    <!-- Optional. List of initialization parameters specific for the adapter. -->

    <!-- Optional (specific for adapters that inherit from
             com.lightstreamer.adapters.metadata.FileBasedProvider).
             search_dir: path of the directory containing all *.items and
             *.schema files, relative to the config directory.
             static: define whether to cache the files or refresh them any time
             they are needed.
             See the FileBasedProvider javadoc. -->
     <!--
    <param name="search_dir">.</param>
    <param name="static">Y</param>
     -->
    <!-- Optional (specific for adapters that inherit from
             com.lightstreamer.adapters.metadata.FileBasedProvider or
             com.lightstreamer.adapters.metadata.LiteralBasedProvider).
             Define values to be returned in getAllowedMaxBandwidth(),
             getAllowedMaxItemFrequency(), getAllowedBufferSize() and
             getDistinctSnapshotLength() methods, for any User and Item
             supplied and optional comma-separated list of User names
             to be allowed by the notifyUser() method.
             See LiteralBasedProvider javadoc. -->
     <!--
    <param name="max_bandwidth">40</param>
    <param name="max_frequency">3</param>
    <param name="buffer_size">30</param>
    <param name="distinct_snapshot_length">10</param>
    <param name="allowed_users">user123,user456</param>
     -->
     <!-- Optional (specific for adapters that inherit from
             com.lightstreamer.adapters.metadata.FileBasedProvider or
             com.lightstreamer.adapters.metadata.LiteralBasedProvider).
             Define how the modeMayBeAllowed method should behave, by
             associating to each item the modes in which it can be managed
             by the Server.
             Each pair of parameters of the form "item_family_<n>" and
             "modes_for_item_family_<n>" define respectively the item name
             pattern (in java.util.regex.Pattern format) and the allowed
             modes (in comma separated format) for a family of items.
             Each item is assigned to the first family that matches its name.
             If no families are specified at all, then modeMayBeAllowed
             always returns true, though this is not recommended, because
             the Server does not support more than one mode out of MERGE,
             DISTINCT and COMMAND for the same item. In such a case, the
             Server would just manage each item in the mode specified by the
             first Client request it receives for the item and would be up to
             the Clients to ensure that the same item cannot be requested in
             two conflicting Modes.
             See LiteralBasedProvider javadoc. -->
    <param name="item_family_1">item.*</param>
    <param name="modes_for_item_family_1">MERGE</param>
    <!--
    <param name="item_family_2">item.*</param>
    <param name="modes_for_item_family_2">MERGE,RAW</param>
     -->
  </metadata_provider>
```

# See Also #

* [Lightstreamer - Portfolio Demo - Java SE Adapter](https://github.com/Weswit/Lightstreamer-example-Portfolio-adapter-java)
* [Lightstreamer - Stock-List Demo - Java SE Adapter](https://github.com/Weswit/Lightstreamer-example-Stocklist-adapter-java)
* [Lightstreamer - Basic Chat Demo - Java SE Adapter](https://github.com/Weswit/Lightstreamer-example-Chat-adapter-java)
* [Lightstreamer - Basic Messenger Demo - Java SE Adapter](https://github.com/Weswit/Lightstreamer-example-Messenger-adapter-java)

# Lightstreamer Compatibility Notes #

- Compatible with Lightstreamer SDK for Java Adapters since 5.1
