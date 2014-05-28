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

import java.io.File;


/**
 * @author nmeneghini
 * @version $Id: ZipDataFilter.java 1 2014-05-01 15:22:47Z nmeneghini $
 */
public class ZipDataFilter extends javax.swing.filechooser.FileFilter {
    public ZipDataFilter() {
    }

    public boolean accept(File file) {
			if (file == null || file.isDirectory()) return true;

			if (file.getAbsolutePath().toLowerCase().endsWith(".zip")) return true;

			return false;
    }

    public String getDescription() {
        return "Zip file";
    }
}
