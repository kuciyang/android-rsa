/*
 * Copyright 2011 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.inftel.androidrsa.utils;

import java.io.File;

import android.os.Environment;

public class AndroidRsaConstants {
    // Constants for directories, images...

    public static final File EXTERNAL_SD_PATH = new File(Environment.getExternalStorageDirectory()
            .getAbsolutePath());
    public static final String SP_KEY_RUN_ONCE = "SP_KEY_RUN_ONCE";
    public static final String FTYPE = ".crt";
    public static final String ENCODED_IMAGE_NAME = "_mobistego";

    // Constants for intents

    public static final String IMAGE_PATH = "img_path";
    public static final String FILE_PATH = "file_path";
    /**
     * PREFERENCES: Se corresponden con las keys definidas en
     * res/xml/preferences.xml
     */
    public static String SHARED_PREFERENCE_FILE = "SHARED_PREFERENCE_FILE";
    public static String KEY_IP_PREFERENCE = "ip_preference";
    public static String KEY_PORT_PREFERENCE = "port_preference";
    public static String KEY_PATH_PREFERENCE = "path_preference";
    public static String KEY_PHONE_NUMBER_PREFERENCE = "phone_number_preference";

}
