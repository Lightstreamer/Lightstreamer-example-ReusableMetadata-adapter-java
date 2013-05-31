/*
*
* Copyright 2013 Weswit s.r.l.
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*
*/

package com.lightstreamer.adapters.metadata;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.lightstreamer.interfaces.metadata.ItemsException;
import com.lightstreamer.interfaces.metadata.MetadataProviderException;
import com.lightstreamer.interfaces.metadata.SchemaException;

/**
 * Simple full implementation of a Metadata Adapter, made available
 * in Lightstreamer SDK. <BR>
 * 
 * The class allows Item Groups and Field Schemas to be stored as simple text files.
 * Any Item Group is stored in a file with the same name and a ".items" extension.
 * Any Field Schema is stored in a file with the same name and a ".schema" extension.
 * The files must list one Item name or Field name per line. Empty lines or
 * lines starting with a "#" (for comments) are also allowed.
 * The files directory has to be supplied together with adapter configuration,
 * through a "search_dir" parameter inside the "metadata_provider" element
 * that defines the Adapter. The files are reloaded at every new request,
 * unless a "static" parameter containing a "Y" is also present. <BR>
 * 
 * The resource levels are assigned the same for all Items and Users,
 * according with values that can be supplied together with adapter
 * configuration, inside the "metadata_provider" element that defines the
 * Adapter. <BR>
 * The return of the getAllowedMaxBandwidth method can be supplied in a
 * "max_bandwidth" parameter; the return of the getAllowedMaxItemFrequency
 * method can be supplied in a "max_frequency" parameter; the return of the
 * getAllowedBufferSize method can be supplied in a "buffer_size" parameter;
 * the return of the getDistinctSnapshotLength method can be supplied
 * in a "distinct_snapshot_length" parameter; the return of the
 * getMinSourceFrequency method can be supplied in a "prefilter_frequency"
 * parameter. All resource limits not supplied are granted as unlimited,
 * but for distinct_snapshot_length, which defaults as 10. <BR>
 * The return of the modeMayBeAllowed method (i.e. the association of the
 * proper publishing Mode to each Item) can be configured by supplying a list
 * of rules, which define Item families, where all Items in each family share
 * the same set of allowed Modes
 * (but remember that only one out of the MERGE, DISTINCT and COMMAND Modes
 * is supported by the Server for each Item; only the RAW mode is supported
 * without restrictions).
 * Each family is specified by providing a pattern upon which all names
 * of the Items in the family need to match.
 * The description of each family can be supplied with a pair of parameters,
 * named "item_family_&lt;n&gt;" and "modes_for_item_family_&lt;n&gt;",
 * where &lt;n&gt; is a progressive number, unique for each family.
 * The former parameter specifies the pattern, in java.util.regex.Pattern
 * format, while the latter one specifies the allowed modes, as a list of
 * names, with commas and spaces as allowed separators.
 * In case more than one of the supplied patterns match an Item name, the
 * Item is assigned to the family with the smallest progressive.
 * Items that do not belong to any family are not allowed in any Mode;
 * however, if no families are defined at all, then all Items are allowed
 * in all Modes and the Clients should ensure that the same Item cannot be
 * requested in two conflicting Modes. <BR>
 * There are no access restrictions, but an optional User name check is
 * performed if a comma separated list of User names is supplied in an
 * "allowed_users" parameter.
 */

public class FileBasedProvider extends LiteralBasedProvider {
    private File myDir;
    private HashMap memory;

    /**
     * Void constructor required by Lightstreamer Kernel.
     */
    public FileBasedProvider() {
    }

    /**
     * Reads configuration settings and sets internal constants.
     * If the setting for "search_dir" is missing, "." is assumed.
     * If the setting for "static" is missing, "Y" is assumed.
     *
     * @param  params  Can contain the configuration settings. 
     * @param  dir  Directory where the configuration file resides.
     * It is used as the base directory for the "search_dir" parameter.
     * @throws MetadataProviderException in case of configuration errors.
     */
    public void init(Map params, File dir) throws MetadataProviderException {
        super.init(params, dir);

        String md = (String) params.get("search_dir");
        if (md != null) {
            myDir = new File(md);
            if (! myDir.isAbsolute()) {
                myDir = new File(dir, md);
            }
        } else {
            myDir = dir;
        }

        String s = (String) params.get("static");
        if ((s != null) && s.equalsIgnoreCase("Y")) {
            memory = new HashMap();
        } else {
            memory = null;
        }
    }

    private String[] read(String fileName) throws IOException {
        if (memory != null) {
            Object found;
            synchronized (memory) {
                found = memory.get(fileName);
                // now the lock is being released;
                // the worst that can happen is that the file
                // is read more than once
            }

            if (found != null) {
                return (String[]) found;
            }
        }

        File myFile = new File(myDir, fileName);
        String[] contents = reallyRead(myFile);

        if (memory != null) {
            synchronized (memory) {
                memory.put(fileName, contents);
            }
        }
        return contents;
    }

    private String[] reallyRead(File myFile) throws IOException {
        ArrayList list = new ArrayList();
        BufferedReader source = null;

        try {
            source = new BufferedReader(new FileReader(myFile));
            String line;

            while (true) {
                line = source.readLine();
                if (line == null) {
                    break;
                }
                line = line.trim();
                if (line.equals("")) {
                    continue;
                }
                if (line.charAt(0) == '#') {
                    continue;    // comment
                }
                list.add(line);
            }
        } finally {
            if (source != null) {
                source.close();
            }
        }
        String[] elems = new String[list.size()];

        for (int i = 0; i < elems.length; i++) {
            elems[i] = (String) list.get(i);
        }
        return elems;
    }

    private String[] readItems(String fileName) throws ItemsException {
        try {
            return read(fileName + ".items");
        } catch (IOException e) {
            throw new ItemsException(e.getClass().getName() + ": "
                                     + e.getMessage());
        }
    }

    private String[] readSchema(String fileName) throws SchemaException {
        try {
            return read(fileName + ".schema");
        } catch (IOException e) {
            throw new SchemaException(e.getClass().getName() + ": "
                                      + e.getMessage());
        }
    }

    /**
     * Resolves an Item Group name supplied in a Request. The names of the Items
     * in the Group are returned.
     * <BR>The operation is deferred to a simpler 2-arguments version of the
     * method, where the sessionID argument is discarded. This also ensures
     * backward compatibility with old adapter classes derived from this one.
     *
     * @param user A User name.
     * @param sessionID A Session ID. Not used.
     * @param group An Item Group name.
     * @return An array with the names of the Items in the Group.
     * @throws ItemsException  if the file does not exists or cannot be read.
     */
    public String[] getItems(String user, String sessionID, String group) throws ItemsException {
        return getItems(user, group);
    }

    /**
     * Resolves an Item Group name supplied in a Request. The names of the Items
     * in the Group are returned.
     * For any supplied Item Group name, a file with the same name and a ".items"
     * extension is open and the Item names are read, one for each line.
     *
     * @param user A User name. Not used.
     * @param group An Item Group name.
     * @return An array with the names of the Items in the Group.
     * @throws ItemsException  if the file does not exists or cannot be read.
     */
    public String[] getItems(String user, String group) throws ItemsException {
        return readItems(group);
    }

    /**
     * Resolves a Field Schema name supplied in a Request. The names of the Fields
     * in the Schema are returned.
      * <BR>The operation is deferred to a simpler 3-arguments version of the
     * method, where the sessionID argument is discarded. This also ensures
     * backward compatibility with old adapter classes derived from this one.
     *
     * @param user A User name.
     * @param sessionID A Session ID. Not used.
     * @param group The name of the Item Group whose Items the Schema
     * is to be applied to.
     * @param schema A Field Schema name.
     * @return An array with the names of the Fields in the Schema.
     * @throws SchemaException  if the file does not exists or cannot be read.
     */
    public String[] getSchema(String user, String sessionID, String group, String schema)
            throws SchemaException {
        return getSchema(user, group, schema);
    }

    /**
     * Resolves a Field Schema name supplied in a Request. The names of the Fields
     * in the Schema are returned.
     * For any supplied Field Schema name, a file with the same name and a ".schema"
     * extension is open and the Field names are read, one for each line.
     *
     * @param user A User name. Not used.
     * @param group The name of the Item Group whose Items the Schema
     * is to be applied to. Not used.
     * @param schema A Field Schema name.
     * @return An array with the names of the Fields in the Schema.
     * @throws SchemaException  if the file does not exists or cannot be read.
     */
    public String[] getSchema(String user, String group, String schema)
            throws SchemaException {
        return readSchema(schema);
    }

}


/*--- Formatted in Lightstreamer Java Convention Style on 2005-03-29 ---*/
