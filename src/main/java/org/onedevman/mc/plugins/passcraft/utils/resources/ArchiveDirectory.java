package org.onedevman.mc.plugins.passcraft.utils.resources;

import org.onedevman.mc.plugins.passcraft.utils.Arrays;
import org.onedevman.mc.plugins.passcraft.utils.Lists;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.zip.ZipEntry;

public class ArchiveDirectory extends ArchiveFile {

    private static int indexOfFile(List<ArchiveFile> files, String filename) {
        int file_count = files.size();
        boolean found = false;

        int i;
        for(i = 0; i < file_count && !(found = files.get(i).name().equals(filename)); ++i);

        if(found)
            return i;

        return -1;
    }

    public static String toString(ArchiveDirectory directory) {
        StringBuilder result = new StringBuilder(ArchiveFile.toString(directory) + " :\n");

        ArchiveFile file;

        int file_count = directory.count();
        for(int i = 0; i < file_count; ++i) {
            file = directory.get(i);
            result.append("    ").append((file.isDir() ? file.asDir() : file).toString().replaceAll("\n", "\n    ")).append("\n");
        }

        result = new StringBuilder(result.substring(0, result.length() - ArchiveFile.PATH_SEPARATOR.length()));

        return result.toString();
    }

    //

    private List<ArchiveFile> _files = new ArrayList<>();

    //

    public ArchiveDirectory(Archive archive, ZipEntry entry) throws ArchiveFileException {
        this(archive, entry, entry.getName());
    }

    public ArchiveDirectory(Archive archive, ZipEntry entry, String dirpath) throws ArchiveFileException {
        super(archive, entry, dirpath, true);
    }

    //

    public List<ArchiveFile> files() {
        return this._files;
    }

    public void add(ArchiveFile file) throws ArchiveFileException {
        if(this.locked())
            throw new ArchiveFileException("Directory locked.");

        this.files().add(file);
    }

    //

    public int count() {
        return this.files().size();
    }

    //

    public ArchiveFile get(int fileindex) {
        return this.files().get(fileindex);
    }

    public ArchiveFile get(String filepath) {
        if(filepath.startsWith(ArchiveFile.PATH_SEPARATOR))
            filepath = filepath.substring(ArchiveFile.PATH_SEPARATOR.length());

        if(filepath.endsWith(ArchiveFile.PATH_SEPARATOR))
            filepath = filepath.substring(0, filepath.length()-ArchiveFile.PATH_SEPARATOR.length());

        String[] path_fragments = filepath.split(ArchiveFile.PATH_SEPARATOR);
        int file_index = indexOfFile(this.files(), path_fragments[0]);

        ArchiveFile file;

        if(file_index > -1) {
            file = this.get(file_index);

            if(path_fragments.length == 1)
                return file;

            if(!file.isDir())
                return null;

            return file.asDir().get(Arrays.join(path_fragments, ArchiveFile.PATH_SEPARATOR, 1));
        }

        return null;
    }

    //

    public ArchiveFile remove(int fileindex) throws ArchiveFileException {
        if(this.locked())
            throw new ArchiveFileException("Directory locked.");

        return this.files().remove(fileindex);
    }

    public ArchiveFile remove(String filepath) throws ArchiveFileException {
        if(this.locked())
            throw new ArchiveFileException("Directory locked.");

        if(filepath.startsWith(ArchiveFile.PATH_SEPARATOR))
            filepath = filepath.substring(ArchiveFile.PATH_SEPARATOR.length());

        if(filepath.endsWith(ArchiveFile.PATH_SEPARATOR))
            filepath = filepath.substring(0, filepath.length()-ArchiveFile.PATH_SEPARATOR.length());

        int file_index = indexOfFile(this.files(), filepath);
        if(file_index > 0)
            return this.remove(file_index);

        return null;
    }

    //

    public boolean has(String filename) {
        return indexOfFile(this.files(), filename) > -1;
    }

    //

    public void extract(String extraction_filepath) throws IOException {
        String[] extracted_filepath_fragments = extraction_filepath.split(PATH_SEPARATOR);
        this.extract(
            extracted_filepath_fragments[extracted_filepath_fragments.length-1],
            Arrays.join(extracted_filepath_fragments, PATH_SEPARATOR, 0, extracted_filepath_fragments.length - 1)
        );
    }

    @Override
    public void extract(String extraction_filename, String extraction_directorypath) throws IOException {
        Path extraction_filepath_path = Paths.get(extraction_directorypath, extraction_filename);
        if(!Files.exists(extraction_filepath_path))
            Files.createDirectories(extraction_filepath_path);

        int file_count = this.count();

        ArchiveFile file;
        for(int i = 0; i < file_count; ++i) {
            file = this.get(i);
            file.extract(file.name(), extraction_filepath_path.toString());
        }
    }

    //

    public void lock(boolean state, boolean recursively) {
        super.lock(state);

        boolean is_files_list_modifiable = Lists.isModifiable(this.files());
        if(this.locked())
            if(is_files_list_modifiable)
                this._files = Collections.unmodifiableList(this.files());
        else
            if(!is_files_list_modifiable)
                this._files = new ArrayList<>(this.files());

        if(recursively) {
            this.files().forEach(file -> {
                if(file.isDir())
                    file.asDir().lock(state, true);
                else
                    file.lock(state);
            });
        }
    }

    //

    @Override
    public String toString() {
        return ArchiveDirectory.toString(this);
    }

}
