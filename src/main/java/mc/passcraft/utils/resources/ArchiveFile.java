package mc.passcraft.utils.resources;

import mc.passcraft.utils.Arrays;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.zip.ZipEntry;

public class ArchiveFile {

    public static final String PATH_SEPARATOR = "/";

    public static String name(ArchiveFile file) {
        return file.path().substring(file.path().lastIndexOf(PATH_SEPARATOR)+1);
    }

    public static String toString(ArchiveFile file) {
        return "("+(file.isDir() ? "directory" : "file")+") "+file.name()+" ["+file.path()+"]";
    }

    //

    private Archive _archive;

    private ZipEntry _zip_entry;
    private String _path;

    private boolean _is_dir = false;

    private boolean _locked = false;

    //

    public ArchiveFile(Archive archive, ZipEntry entry) throws ArchiveFileException {
        this(archive, entry, entry.getName());
    }

    public ArchiveFile(Archive archive, ZipEntry entry, String filepath) throws ArchiveFileException {
        this(archive, entry, filepath, false);
    }

    public ArchiveFile(Archive archive, ZipEntry entry, String filepath, boolean isDirectory) throws ArchiveFileException {
        this._archive = archive;

        this.zipEntry(entry);
        this.path(filepath);

        this._is_dir = isDirectory;
    }

    //

    public Archive archive() { return this._archive; }

    public ZipEntry zipEntry() { return this._zip_entry; }
    public ZipEntry zipEntry(ZipEntry entry) throws ArchiveFileException {
        if(this.locked())
            throw new ArchiveFileException((this.isDir() ? "Directory" : "File")+" locked.");

        return this._zip_entry = entry;
    }

    public String path() { return this._path; }
    public String path(String path) throws ArchiveFileException {
        if(this.locked())
            throw new ArchiveFileException((this.isDir() ? "Directory" : "File")+" locked.");

        return this._path = path;
    }

    public String name() {
        return ArchiveFile.name(this);
    }

    //

    public boolean isDir() { return this._is_dir; }

    public ArchiveDirectory asDir() { return this.isDir() ? (ArchiveDirectory) this : null; }

    //

    public InputStream content() throws IOException {
        if(this.isDir())
            return null;

        return this.archive().zip().getInputStream(this.zipEntry());
    }

    //

    public void extract(String extraction_filepath) throws IOException {
        String[] extracted_filepath_fragments = extraction_filepath.split(PATH_SEPARATOR);
        this.extract(
            extracted_filepath_fragments[extracted_filepath_fragments.length-1],
            Arrays.join(extracted_filepath_fragments, PATH_SEPARATOR, 0, extracted_filepath_fragments.length - 1)
        );
    }

    public void extract(String extraction_filename, String extraction_directorypath) throws IOException {
        Path extraction_directorypath_path = Path.of(extraction_directorypath);
        if(!Files.exists(extraction_directorypath_path))
            Files.createDirectories(extraction_directorypath_path);

        FileOutputStream output = null;
        try {
            output = new FileOutputStream(Paths.get(extraction_directorypath, extraction_filename).toString());
            InputStream content = this.content();

            byte[] buf = new byte[1024];
            int length;

            while ((length = content.read(buf, 0, buf.length)) >= 0)
                output.write(buf, 0, length);

            output.close();
        } catch (IOException ioex) {
            if(output != null)
                output.close();
        }
    }

    //

    public boolean locked() { return this._locked; }

    public void lock(boolean state) { this._locked = state; }

    //

    @Override
    public String toString() {
        return ArchiveFile.toString(this);
    }
}
