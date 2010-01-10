package org.wyona.commons.io;

import java.io.File;

import org.apache.log4j.Logger;

/**
 *
 */
public class Sync {

    private static Logger log = Logger.getLogger(Sync.class);

    /**
     * Synchronize two directories/volumes recursively
     * @param source Source directory
     * @param destination Destination directory
     * @param excludes Comma separated list of directories and files which should be excluded from synchronization
     * @param ignoreHidden Ignore hidden files from synchronization
     */
    public void synchronize(File source, File destination, String excludes, boolean ignoreHidden) {
        if (!source.isDirectory()) {
            log.error("No such source directory: " + source.getAbsolutePath());
            return;
        }
        if (!destination.isDirectory()) {
            log.error("No such destination directory: " + destination.getAbsolutePath());
            return;
        }
        if (destination.compareTo(source) == 0) {
            log.error("Source and destination directory are the same '" + source.getAbsolutePath() + "' and hence synchronization aborted!");
            return;
        }

        log.error("Synchronizing (Source: '" + source + "', Destination: '" + destination + "') ...");
        doSynchronize(source, destination, ignoreHidden);
        log.error("Synchronization finished.");
    }

    /**
     *
     */
    private void doSynchronize(File source, File destination, boolean ignoreHidden) {
        if (!source.canRead()) {
            log.warn("Permission denied: " + source);
            return;
        }
        if (ignoreHidden && source.isHidden()) {
            log.warn("Hidden file or directory: " + source);
            return;
        }
        String[] filesAndDirs = source.list();
        if (filesAndDirs != null) { // TODO: filesAndDirs can be null for example if permission denied
            for (int i = 0; i < filesAndDirs.length; i++) {
                File fd = new File(source, filesAndDirs[i]);
                if (fd.isFile()) {
                    //log.warn("DEBUG: File: " + fd.getName());
                    if (!new File(destination, fd.getName()).isFile()) {
                        log.warn("No such file at destination: " + new File(destination, fd.getName()));
                    }
                } else if (fd.isDirectory()) {
                    //log.warn("DEBUG: Directory: " + fd.getAbsolutePath());
                    if (!new File(destination, fd.getName()).isDirectory()) {
                        log.warn("No such directory at destination: " + new File(destination, fd.getName()));
                    } else {
                        doSynchronize(new File(source, fd.getName()), new File(destination, fd.getName()), ignoreHidden);
                    }
                } else {
                    log.warn("Neither file nor directory: " + fd.getAbsolutePath());
                }
            }
        } else {
            log.warn("No children found within: " + source);
        }
    }
}
