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
package it.trilogis.worldwind.tilecreation.constants;

/**
 * @author nmeneghini
 * @version $Id: TileConstants.java 1 2014-05-01 15:22:47Z nmeneghini $
 */
public class TileConstants {
    public static final double LEVEL_ZERO_TILE_DELTA = 36d;
    public static final double TILE_ORIGIN_LATITUDE = -90d;
    public static final double TILE_ORIGIN_LONGITUDE = -180d;
    // Fixed values for LANDSAT LAYER
    public static final String LANDSAT_WMS_URL = "http://data.worldwind.arc.nasa.gov/wms";
    public static final String LANDSAT_WMS_LAYERNAME = "esat";
    public static final int LANDSAT_STARTING_LEVEL = 4;
    public static final int LANDSAT_CACHINGLEVELS = 6;
    public static final int LANDSAT_TILESIZE = 512;
    //Fixed values for Elevation
    public static final String ELEVATION_WMS_URL = "http://data.worldwind.arc.nasa.gov/elev";
    public static final String ELEVATION_WMS_LAYERNAME = "mergedAsterElevations";
    public static final int ELEVATION_STARTING_LEVEL = 0;
    public static final int ELEVATION_CACHINGLEVELS = 10;
    public static final int ELEVATION_LEVEL_ZERO_TILE_DELTA = 20;
    public static final int ELEVATION_TILESIZE = 150;
}
