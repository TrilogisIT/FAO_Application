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
package it.trilogis.worldwind.tilecreation.datafilters;

import gov.nasa.worldwindx.examples.dataimport.DataInstallUtil;

import java.io.File;

/**
 * @author nmeneghini
 * @version $Id: InstallableDataFilter.java 1 2014-05-01 15:22:47Z nmeneghini $
 */
public class InstallableDataFilter extends javax.swing.filechooser.FileFilter {
    public InstallableDataFilter() {
    }

    @Override
    public boolean accept(File file) {
		if (file == null || file.isDirectory()) return true;

		if (DataInstallUtil.isDataRaster(file, null)) return true;
		else if (DataInstallUtil.isWWDotNetLayerSet(file)) return true;

		return false;
		
    }

    @Override
    public String getDescription() {
        return "Supported Images/Elevations";
    }

}
