/*
 * Copyright (C) 2014 Trilogis S.r.l.
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
package it.trilogis.worldwind.tilecreation.properties;

import it.trilogis.worldwind.tilecreation.constants.PropertiesConstants;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Properties;

/**
 * Property manager class for managing properties
 * 
 * @author meneghini
 */
public class PropertiesManager {
    private static Properties applicationProperties;

    private static String splitString = ";";

    public static Properties getApplicationProperties() {
        if (applicationProperties == null) {
            applicationProperties = new Properties();
        }
        return applicationProperties;
    }

    public static void initProperties() {
        Properties defaultProperties = new Properties();
        defaultProperties.setProperty(PropertiesConstants.PROPERTY_MAX_LAT, Double.toString(PropertiesConstants.DEFAULT_MAX_LAT));
        defaultProperties.setProperty(PropertiesConstants.PROPERTY_MAX_LON, Double.toString(PropertiesConstants.DEFAULT_MAX_LON));
        defaultProperties.setProperty(PropertiesConstants.PROPERTY_MIN_LAT, Double.toString(PropertiesConstants.DEFAULT_MIN_LAT));
        defaultProperties.setProperty(PropertiesConstants.PROPERTY_MIN_LON, Double.toString(PropertiesConstants.DEFAULT_MIN_LON));
        defaultProperties.setProperty(PropertiesConstants.PROPERTY_TILES_DIRECTORY, PropertiesConstants.DEFAULT_TILES_DIRECTORY);
        defaultProperties.setProperty(PropertiesConstants.PROPERTY_GREENNESS_DIRECTORY, PropertiesConstants.DEFAULT_GREENNESS_DIRECTORY);
        defaultProperties.setProperty(PropertiesConstants.PROPERTY_RAINFALL_DIRECTORY, PropertiesConstants.DEFAULT_RAINFALL_DIRECTORY);
        defaultProperties.setProperty(PropertiesConstants.PROPERTY_LANDSAT_DIRECTORY, PropertiesConstants.DEFAULT_LANDSAT_DIRECTORY);
        defaultProperties.setProperty(PropertiesConstants.PROPERTY_TPC_DIRECTORY, PropertiesConstants.DEFAULT_TPC_DIRECTORY);
        defaultProperties.setProperty(PropertiesConstants.PROPERTY_ELEVATION_DIRECTORY, PropertiesConstants.DEFAULT_ELEVATION_DIRECTORY);
        defaultProperties.setProperty(PropertiesConstants.PROPERTY_BOUNDARIES_DIRECTORY, PropertiesConstants.DEFAULT_BOUNDARIES_DIRECTORY);
        defaultProperties.setProperty(PropertiesConstants.PROPERTY_GREENNESS_FILES, PropertiesConstants.DEFAULT_GREENNESS_FILES);
        defaultProperties.setProperty(PropertiesConstants.PROPERTY_RAINFALL_FILES, PropertiesConstants.DEFAULT_RAINFALL_FILES);
        defaultProperties.setProperty(PropertiesConstants.PROPERTY_LANDSAT_FILES, PropertiesConstants.DEFAULT_LANDSAT_FILES);
        defaultProperties.setProperty(PropertiesConstants.PROPERTY_TPC_FILES, PropertiesConstants.DEFAULT_TPC_FILES);
        defaultProperties.setProperty(PropertiesConstants.PROPERTY_ELEVATION_FILES, PropertiesConstants.DEFAULT_ELEVATION_FILES);
        defaultProperties.setProperty(PropertiesConstants.PROPERTY_BOUNDARIES_FILES, PropertiesConstants.DEFAULT_BOUNDARIES_FILES);
        try {
            File propertiesFile = getPropertiesFile();
            applicationProperties = getPropertiesFromFile(defaultProperties, propertiesFile.getAbsolutePath());
            if (null != applicationProperties) {
                for (String propName : defaultProperties.stringPropertyNames()) {
                    String propValue = applicationProperties.getProperty(propName);
                    if (propValue != null) {
                        applicationProperties.setProperty(propName, propValue);
                    }
                }
            }
            writePropertiesToFile(applicationProperties);
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    public static File getPropertiesFile() throws IllegalStateException, IOException, URISyntaxException {
        // File f = new File(PropertiesManager.class.getProtectionDomain().getCodeSource().getLocation().toURI());
        // File home = f.getAbsoluteFile().getParentFile();
        File home = new File(System.getProperty("user.home"));//TODO uncomment upper lines, comment this line

        File propertiesFile = new File(home, PropertiesConstants.PROPERTIES_FILE_NAME);
        if (!propertiesFile.exists()) {
            if (!propertiesFile.createNewFile()) {
                throw new IOException("Can't create the file " + propertiesFile.toString());
            }
        }
        return propertiesFile;
    }

    public static Properties getPropertiesFromFile(Properties defaultProperties, String propertiesFileName) {
        // Read properties file.
        Properties prop = new Properties(defaultProperties);
        try {
            prop.load(new FileInputStream(propertiesFileName));
        } catch (IOException e) {
            writePropertiesToFile(prop);
        }
        return prop;
    }

    public static Properties getPropertiesFromFile(Properties defaultProperties, File propertiesFile) {
        // Read properties file.
        Properties prop = new Properties(defaultProperties);
        try {
            prop.load(new FileInputStream(propertiesFile));
        } catch (IOException e) {
            writePropertiesToFile(prop);
        }
        return prop;
    }

    public static Properties getPropertiesFromFile(File fileProperties) {
        Properties prop = new Properties(getApplicationProperties());
        try {
            prop.load(new FileInputStream(fileProperties));
        } catch (IOException e) {
            writePropertiesToFile(prop);
        }
        return prop;
    }

    public static void setPropertiesFromFile(File fileProperties) {
        try {
            applicationProperties.load(new FileInputStream(fileProperties));
        } catch (IOException e) {
        }
    }

    public static boolean writePropertiesToFile(Properties prop) {
        // Write properties file.
        try {
            String propertiesFileName = getPropertiesFile().getAbsolutePath();
            prop.store(new FileOutputStream(propertiesFileName), null);
            return true;
        } catch (IOException e) {
            return false;
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean setStringProperty(String propertyName, String propertyValue) {
        if (propertyValue == null)
            propertyName = "";
        getApplicationProperties().setProperty(propertyName, propertyValue);
        writePropertiesToFile(getApplicationProperties());
        return true;
    }

    public static boolean setArrayStringProperty(String propertyName, String[] propertyValueList) {
        String propertyValue = "";
        if (!(propertyValueList == null || propertyValueList.length == 0)) {
            for (int i = 0; i < propertyValueList.length; i++) {
                propertyValue = propertyValue + propertyValueList[i] + splitString;
            }
            propertyValue = propertyValue.substring(0, propertyValue.length() - splitString.length());
        }
        getApplicationProperties().setProperty(propertyName, propertyValue);
        writePropertiesToFile(getApplicationProperties());
        return true;
    }

    public static String getStringProperty(String propertyName) {
        return getApplicationProperties().getProperty(propertyName);
    }

    public static Double getDoubleProperty(String propertyName) {
        String value = getApplicationProperties().getProperty(propertyName);
        try {
            return Double.parseDouble(value);
        } catch (Exception e) {
        }
        return null;
    }

    public static double getdoubleProperty(String propertyName) {
        String value = getApplicationProperties().getProperty(propertyName);
        try {
            return Double.parseDouble(value);
        } catch (Exception e) {
        }
        return 0;
    }

    public static boolean getBooleanProperty(String propertyName) {
        String value = getApplicationProperties().getProperty(propertyName);
        try {
            return Boolean.parseBoolean(value);
        } catch (Exception e) {
        }
        return false;
    }

    public static String[] getArrayStringProperty(String propertyName) {
        String value = getApplicationProperties().getProperty(propertyName);
        if (value == null || value.length() == 0) {
            return null;
        }
        return value.split(splitString);
    }

}
