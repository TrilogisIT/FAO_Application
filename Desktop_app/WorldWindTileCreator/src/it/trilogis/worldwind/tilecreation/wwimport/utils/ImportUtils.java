/*
Copyright (C) 2001, 2011 United States Government as represented by
the Administrator of the National Aeronautics and Space Administration.
All Rights Reserved.
 */
package it.trilogis.worldwind.tilecreation.wwimport.utils;

import gov.nasa.worldwind.Configuration;
import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwind.avlist.AVList;
import gov.nasa.worldwind.avlist.AVListImpl;
import gov.nasa.worldwind.data.CachedDataRaster;
import gov.nasa.worldwind.data.DataRaster;
import gov.nasa.worldwind.data.DataStoreProducer;
import gov.nasa.worldwind.data.TiledElevationProducer;
import gov.nasa.worldwind.data.TiledPKMImageProducer;
import gov.nasa.worldwind.data.TiledRasterProducer;
import gov.nasa.worldwind.data.TransparentPKMTiledImageProducer;
import gov.nasa.worldwind.data.WWDotNetLayerSetConverter;
import gov.nasa.worldwind.exception.WWRuntimeException;
import gov.nasa.worldwind.geom.Sector;
import gov.nasa.worldwind.util.Logging;
import gov.nasa.worldwind.util.WWIO;
import gov.nasa.worldwind.util.WWUtil;
import gov.nasa.worldwind.util.WWXML;
import gov.nasa.worldwindx.examples.dataimport.DataInstallUtil;
import java.io.File;
import java.util.Map;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/*
 * @author dcollins
 */
public class ImportUtils {
    public static Document createDataStore(File[] files, File directory, DataStoreProducer producer, String datasetName) throws Exception {
        // File installLocation = DataInstallUtil.getDefaultInstallLocation(fileStore);
        if (directory == null) {
            String message = Logging.getMessage("generic.NoDefaultImportLocation");
            Logging.logger().severe(message);
            return null;
        }

        // Create the production parameters. These parameters instruct the DataStoreProducer where to install the cached
        // data, and what name to put in the data configuration document.
        AVList params = new AVListImpl();

        params.setValue(AVKey.DATASET_NAME, datasetName);
        params.setValue(AVKey.DATA_CACHE_NAME, datasetName);
        params.setValue(AVKey.FILE_STORE_LOCATION, directory.getAbsolutePath());

        // These parameters define producer's behavior:
        // create a full tile cache OR generate only first two low resolution levels
        boolean enableFullPyramid = Configuration.getBooleanValue(AVKey.PRODUCER_ENABLE_FULL_PYRAMID, true);
        if (!enableFullPyramid) {
            params.setValue(AVKey.SERVICE_NAME, AVKey.SERVICE_NAME_LOCAL_RASTER_SERVER);
            // retrieve the value of the AVKey.TILED_RASTER_PRODUCER_LIMIT_MAX_LEVEL, default to "Auto" if missing
            String maxLevel = Configuration.getStringValue(AVKey.TILED_RASTER_PRODUCER_LIMIT_MAX_LEVEL, "Auto");
            params.setValue(AVKey.TILED_RASTER_PRODUCER_LIMIT_MAX_LEVEL, maxLevel);
        } else {
            params.setValue(AVKey.PRODUCER_ENABLE_FULL_PYRAMID, true);
        }

        producer.setStoreParameters(params);

        try {
            for (File file : files) {
                producer.offerDataSource(file, null);
                Thread.yield();
            }

            // Convert the file to a form usable by World Wind components, according to the specified DataStoreProducer.
            // This throws an exception if production fails for any reason.
            producer.startProduction();
        } catch (InterruptedException ie) {
            producer.removeProductionState();
            Thread.interrupted();
            throw ie;
        } catch (Exception e) {
            // Exception attempting to convert the file. Revert any change made during production.
            producer.removeProductionState();
            throw e;
        }

        // Return the DataConfiguration from the production results. Since production successfully completed, the
        // DataStoreProducer should contain a DataConfiguration in the production results. We test the production
        // results anyway.
        Iterable<?> results = producer.getProductionResults();
        if (results != null && results.iterator() != null && results.iterator().hasNext()) {
            Object o = results.iterator().next();
            if (o != null && o instanceof Document) {
                return (Document) o;
            }
        }

        return null;
    }

    public static void createRasterServerConfigDoc(File directory, DataStoreProducer producer) {
        AVList productionParams = (null != producer) ? producer.getProductionParameters() : new AVListImpl();
        productionParams = (null == productionParams) ? new AVListImpl() : productionParams;

        if (!AVKey.SERVICE_NAME_LOCAL_RASTER_SERVER.equals(productionParams.getValue(AVKey.SERVICE_NAME))) {
            // *.RasterServer.xml is not required
            return;
        }

        if (directory == null) {
            String message = Logging.getMessage("generic.NoDefaultImportLocation");
            Logging.logger().severe(message);
            return;
        }

        Document doc = WWXML.createDocumentBuilder(true).newDocument();

        Element root = WWXML.setDocumentElement(doc, "RasterServer");
        WWXML.setTextAttribute(root, "version", "1.0");

        StringBuilder sb = new StringBuilder();
        sb.append(directory.getAbsolutePath()).append(File.separator);

        if (!productionParams.hasKey(AVKey.DATA_CACHE_NAME)) {
            String message = Logging.getMessage("generic.MissingRequiredParameter", AVKey.DATA_CACHE_NAME);
            Logging.logger().severe(message);
            throw new WWRuntimeException(message);
        }
        sb.append(productionParams.getValue(AVKey.DATA_CACHE_NAME)).append(File.separator);

        if (!productionParams.hasKey(AVKey.DATASET_NAME)) {
            String message = Logging.getMessage("generic.MissingRequiredParameter", AVKey.DATASET_NAME);
            Logging.logger().severe(message);
            throw new WWRuntimeException(message);
        }
        sb.append(productionParams.getValue(AVKey.DATASET_NAME)).append(".RasterServer.xml");

        Object o = productionParams.getValue(AVKey.DISPLAY_NAME);
        if (WWUtil.isEmpty(o))
            productionParams.setValue(AVKey.DISPLAY_NAME, productionParams.getValue(AVKey.DATASET_NAME));

        String rasterServerConfigFilePath = sb.toString();

        Sector extent = null;
        if (productionParams.hasKey(AVKey.SECTOR)) {
            o = productionParams.getValue(AVKey.SECTOR);
            if (null != o && o instanceof Sector)
                extent = (Sector) o;
        }

        if (null != extent)
            WWXML.appendSector(root, "Sector", extent);
        else {
            String message = Logging.getMessage("generic.MissingRequiredParameter", AVKey.SECTOR);
            Logging.logger().severe(message);
            throw new WWRuntimeException(message);
        }

        Element sources = doc.createElementNS(null, "Sources");
        if (producer instanceof TiledRasterProducer) {
            for (DataRaster raster : ((TiledRasterProducer) producer).getDataRasters()) {
                if (raster instanceof CachedDataRaster) {
                    try {
                        appendSource(sources, (CachedDataRaster) raster);
                    } catch (Throwable t) {
                        String reason = WWUtil.extractExceptionReason(t);
                        Logging.logger().warning(reason);
                        // Logging.logger().severe(reason);
                        // throw new WWRuntimeException(reason);
                    }
                } else {
                    String message = Logging.getMessage("TiledRasterProducer.UnrecognizedRasterType", raster.getClass().getName(), raster.getStringValue(AVKey.DATASET_NAME));
                    Logging.logger().severe(message);
                    throw new WWRuntimeException(message);
                }
            }
        }

        AVList rasterServerProperties = new AVListImpl();

        String[] keysToCopy = new String[] { AVKey.DATA_CACHE_NAME, AVKey.DATASET_NAME, AVKey.DISPLAY_NAME };
        WWUtil.copyValues(productionParams, rasterServerProperties, keysToCopy, false);

        appendProperties(root, rasterServerProperties);

        // add sources
        root.appendChild(sources);

        WWXML.saveDocumentToFile(doc, rasterServerConfigFilePath);
    }

    /**
     * Append Property elements to a context element.
     * 
     * @param context
     *            the context on which to append new element(s)
     * @param properties
     *            AVList with properties to append.
     */
    public static void appendProperties(Element context, AVList properties) {
        if (null == context || properties == null)
            return;

        StringBuilder sb = new StringBuilder();

        // add properties
        for (Map.Entry<String, Object> entry : properties.getEntries()) {
            sb.setLength(0);
            String key = entry.getKey();
            sb.append(properties.getValue(key));
            String value = sb.toString();
            if (WWUtil.isEmpty(key) || WWUtil.isEmpty(value))
                continue;

            Element property = WWXML.appendElement(context, "Property");
            WWXML.setTextAttribute(property, "name", key);
            WWXML.setTextAttribute(property, "value", value);
        }
    }

    /**
     * Append Source element to a Sources element.
     * 
     * @param sources
     *            the Sources element on which to append the new Source element
     * @param raster
     *            instance of the CachedDataRaster
     * @throws WWRuntimeException
     *             if cannot retrieve or understand the data source
     */
    public static void appendSource(Element sources, CachedDataRaster raster) throws WWRuntimeException {
        Object o = raster.getDataSource();
        if (WWUtil.isEmpty(o)) {
            String message = Logging.getMessage("nullValue.DataSourceIsNull");
            Logging.logger().fine(message);
            throw new WWRuntimeException(message);
        }

        File f = WWIO.getFileForLocalAddress(o);
        if (WWUtil.isEmpty(f)) {
            String message = Logging.getMessage("TiledRasterProducer.UnrecognizedDataSource", o);
            Logging.logger().fine(message);
            throw new WWRuntimeException(message);
        }

        Element source = WWXML.appendElement(sources, "Source");
        WWXML.setTextAttribute(source, "type", "file");
        WWXML.setTextAttribute(source, "path", f.getAbsolutePath());

        AVList params = raster.getParams();
        if (null == params) {
            String message = Logging.getMessage("nullValue.ParamsIsNull");
            Logging.logger().fine(message);
            throw new WWRuntimeException(message);
        }

        Sector sector = raster.getSector();
        if (null == sector && params.hasKey(AVKey.SECTOR)) {
            o = params.getValue(AVKey.SECTOR);
            if (o instanceof Sector)
                sector = (Sector) o;
        }

        if (null != sector)
            WWXML.appendSector(source, "Sector", sector);
    }

    public static DataStoreProducer createDataStoreProducerFromFiles(File[] files, Sector bbox, boolean makeBackgroundNonTransparent) throws IllegalArgumentException {
        if (files == null || files.length == 0) {
            String message = Logging.getMessage("nullValue.ArrayIsNull");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        String commonPixelFormat = null;
        for (File file : files) {
            AVList params = new AVListImpl();
            if (DataInstallUtil.isDataRaster(file, params)) {

                String pixelFormat = params.getStringValue(AVKey.PIXEL_FORMAT);
                if (WWUtil.isEmpty(commonPixelFormat)) {
                    if (WWUtil.isEmpty(pixelFormat)) {
                        String message = Logging.getMessage("generic.UnrecognizedSourceType", file.getAbsolutePath());
                        Logging.logger().severe(message);
                        throw new IllegalArgumentException(message);
                    } else {
                        commonPixelFormat = pixelFormat;
                    }
                } else if (commonPixelFormat != null && !commonPixelFormat.equals(pixelFormat)) {
                    if (WWUtil.isEmpty(pixelFormat)) {
                        String message = Logging.getMessage("generic.UnrecognizedSourceType", file.getAbsolutePath());
                        Logging.logger().severe(message);
                        throw new IllegalArgumentException(message);
                    } else {
                        String reason = Logging.getMessage("generic.UnexpectedRasterType", pixelFormat);
                        String details = file.getAbsolutePath() + ": " + reason;
                        String message = Logging.getMessage("DataRaster.IncompatibleRaster", details);
                        Logging.logger().severe(message);
                        throw new IllegalArgumentException(message);
                    }
                }
            } else if (DataInstallUtil.isWWDotNetLayerSet(file)) {
                // you cannot select multiple World Wind .NET Layer Sets
                // bail out on a first raster
                return new WWDotNetLayerSetConverter();
            }
        }

        if (AVKey.IMAGE.equals(commonPixelFormat)) {
            if (makeBackgroundNonTransparent) {
                return new TiledPKMImageProducer(bbox);
            } else {
                return new TransparentPKMTiledImageProducer(bbox);
            }
            // return new TransparentPKMTiledImageProducer(bbox);
        } else if (AVKey.ELEVATION.equals(commonPixelFormat)) {
            return new TiledElevationProducer(bbox);
        }

        String message = Logging.getMessage("generic.UnexpectedRasterType", commonPixelFormat);
        Logging.logger().severe(message);
        throw new IllegalArgumentException(message);
    }

}
