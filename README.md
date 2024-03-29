# Lightstreamer - Reusable Metadata Adapters - Java Adapter

<!-- START DESCRIPTION lightstreamer-example-reusablemetadata-adapter-java -->

<b>WARNING. This project is obsolete, the relevant code has been merged into this project [Lightstreamer Java In-Process Adapter SDK](https://github.com/Lightstreamer/Lightstreamer-lib-adapter-java-inprocess); which you need to refer to for an updated version of the LiteralBasedProvider Metadata Adapter.</b>

This project includes two simple full implementations of Metadata Adapter in Java made available as sample for inspiration and/or extension.

## LiteralBasedProvider Metadata Adapter

The LiteralBasedProvider extends the MetadataProviderAdapter abstract class (which in turn implements the MetadataProvider interface). It is used in many Lightstreamer examples and demos, in combination with different Data Adapters and Clients.
It is also useful when developing proof of concepts and prototypes, where the main focus is on the Data Adapter.
Its binaries are included in the Server distribution.

## FileBasedProvider Metadata Adapter

The FileBasedProvider extends the LiteralBasedProvider, but is rarely used.
<!-- END DESCRIPTION lightstreamer-example-reusablemetadata-adapter-java -->
<br>
<br>

## Build

Before you can compile the adapters, some dependencies need to be solved:
* Get the `ls-adapter-interface.jar` file from the [Lightstreamer 6 distribution](http://www.lightstreamer.com/download), you can find it in the `Lightstreamer/DOCS-SDKs/sdk_adapter_java_inprocess/lib` folder, and put it in a temporary folder, let's call it `compile_libs`.

Note that `ls-adapter-interface.jar` already includes the class files for com.lightstreamer.adapters.metadata.LiteralBasedProvider; we can ignore that for a moment.
If you are testing your own modified version of the LiteralBasedProvider code, take care of changing the package name, or, at least, the class name.

Now you can generate the jar for the sample Metadata Adapters, let's call it `ls-generic-adapters.jar`, with the following commands:
```sh
  >mkdir tmp_classes

  >javac -source 1.7 -target 1.7 -nowarn -g -classpath compile_libs/ls-adapter-interface.jar -sourcepath src -d tmp_classes src/com/lightstreamer/adapters/metadata/LiteralBasedProvider.java src/com/lightstreamer/adapters/metadata/FileBasedProvider.java

  >jar cvf ls-generic-adapters.jar -C tmp_classes com/lightstreamer
```

### Deploy

To use one of the Metadata Adapters just built in some adapter set, just copy the `ls-generic-adapters.jar` file to the `lib` directory of the Adapter Set installation.
To use these Metadata Adapters in multiple Adapter Sets, you may also copy the `ls-generic-adapters.jar` file in the `shared/lib` directory of your Lightstreamer Server installation. 
As said, the class files for com.lightstreamer.adapters.metadata.LiteralBasedProvider are already included in `ls-adapter-interface.jar`, which is part of Lightstreamer Server installation; hence this step is not needed for the LiteralBasedProvider.

Then configure the right Metadata provider and its properties in the `adapters.xml` descriptor file within your adapters' subfolder. The following code snippet shows an example: 
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
             DISTINCT, and COMMAND for the same item. In such a case, the
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

Above, are shown only the specific parameters of *LiteralBasedProvider* Metadata Adapter. Use the generic template (see the [Java In-Process Adapter Interface Project](https://github.com/Lightstreamer/Lightstreamer-lib-adapter-java-inprocess#configuration) ) as a reference for a complete overview of configuration options.

## See Also
<!-- START RELATED_ENTRIES -->

* [Lightstreamer - Portfolio Demo - Java Adapter](https://github.com/Lightstreamer/Lightstreamer-example-Portfolio-adapter-java)
* [Lightstreamer - Stock-List Demo - Java Adapter](https://github.com/Lightstreamer/Lightstreamer-example-Stocklist-adapter-java)
* [Lightstreamer - Basic Chat Demo - Java Adapter](https://github.com/Lightstreamer/Lightstreamer-example-Chat-adapter-java)
* [Lightstreamer - Basic Messenger Demo - Java Adapter](https://github.com/Lightstreamer/Lightstreamer-example-Messenger-adapter-java)
* [Lightstreamer - Room-Ball Demo - Java Adapter](https://github.com/Lightstreamer/Lightstreamer-example-RoomBall-adapter-java)

<!-- END RELATED_ENTRIES -->

## Lightstreamer Compatibility Notes

* Compatible with Lightstreamer SDK for Java In-Process Adapters from version 6.0 to 7.2.
- For a version of this example compatible with Lightstreamer SDK for Java Adapters version 7.3.0 or later, please refer to [this project](https://github.com/Lightstreamer/Lightstreamer-lib-adapter-java-inprocess).
- For a version of this example compatible with Lightstreamer SDK for Java Adapters version 5.1, please refer to [this tag](https://github.com/Lightstreamer/Lightstreamer-example-ReusableMetadata-adapter-java/tree/for_Lightstreamer_5.1).
