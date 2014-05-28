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
package it.trilogis.worldwind.tilecreation.swing;

import it.trilogis.worldwind.tilecreation.utils.FileUtils;
import java.io.File;
import javax.swing.SwingWorker;

/**
 * @author nmeneghini
 * @version $Id: ZipDirectorySwingWorker.java 1 2014-05-01 15:22:47Z nmeneghini $
 */
public class ZipDirectorySwingWorker extends SwingWorker<Boolean, Integer> {

    private String zipFileName;
    private File cacheDirectory;

    public ZipDirectorySwingWorker(File cacheDir, String zipFile) {
        this.cacheDirectory = cacheDir;
        this.zipFileName = zipFile;
    }

    /*
     * (non-Javadoc)
     * @see javax.swing.SwingWorker#doInBackground()
     */
    @Override
    protected Boolean doInBackground() throws Exception {
        // if you have both landsat and custom, create package
        setProgress(20);
        boolean success = FileUtils.createZipFile(cacheDirectory, zipFileName);
        setProgress(100);
        return success;
    }

}
