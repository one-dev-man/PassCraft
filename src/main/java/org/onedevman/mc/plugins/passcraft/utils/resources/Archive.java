package org.onedevman.mc.plugins.passcraft.utils.resources;

import org.onedevman.mc.plugins.passcraft.utils.Arrays;

import java.io.IOException;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class Archive extends ArchiveDirectory {

    private ZipFile _zip;

    //

    public Archive(String path) throws IOException, ArchiveFileException {
        this(new ZipFile(path));
    }

    public Archive(ZipFile zip) throws ArchiveFileException, IOException {
        super(null, null, null);
        this._zip = zip;

        this.update();
    }

    //

    @Override
    public String name() {
        return ArchiveFile.name(this);
    }

    //

    public void update() throws ArchiveFileException, IOException {
        this._zip = new ZipFile(this.path());

        this.lock(false);

        //

        ArchiveDirectory directory;

        ArchiveFile file;
        String filepath;
        String filename;
        String[] filepath_fragments;

        Enumeration<? extends ZipEntry> entries = this.zip().entries();

        ZipEntry entry;
        while(entries.hasMoreElements()) {
            entry = entries.nextElement();

            filepath = entry.getName();
            if(filepath.endsWith(ArchiveFile.PATH_SEPARATOR))
                filepath = filepath.substring(0, filepath.length()-ArchiveFile.PATH_SEPARATOR.length());

            filepath_fragments = filepath.split(ArchiveFile.PATH_SEPARATOR);
            filename = filepath_fragments[filepath_fragments.length-1];

            directory = this;
            for(int i = 0; i < filepath_fragments.length-1; ++i) {
                ArchiveFile path_searching_file_directory = directory.get(filepath_fragments[i]);

                if(path_searching_file_directory == null) {
                    path_searching_file_directory = new ArchiveDirectory(this, null, Arrays.join(filepath_fragments, ArchiveFile.PATH_SEPARATOR, 0, i+1));
                    directory.add(path_searching_file_directory);
                }

                if(path_searching_file_directory.isDir())
                    directory = path_searching_file_directory.asDir();
            }

            file = directory.get(filename);

            if(file == null) {
                file = entry.isDirectory() ? new ArchiveDirectory(this, entry) : new ArchiveFile(this, entry);
                directory.add(file);
            }
            else {
                file.zipEntry(entry);
                file.path(filepath);
            }
        }

        //

        this.lock(true);
    }

    //

    public ZipFile zip() { return this._zip; }

    @Override
    public String path() { return this.zip().getName(); }

}