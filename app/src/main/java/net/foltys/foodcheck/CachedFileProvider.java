package net.foltys.foodcheck;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;

public class CachedFileProvider extends ContentProvider {

    // The authority is the symbolic name for the provider class
    public static final String AUTHORITY = "net.foltys.foodcheck.provider";
    private static final String CLASS_NAME = "CachedFileProvider";
    // UriMatcher used to match against incoming requests
    private UriMatcher uriMatcher;

    /**
     * Initializes content provider on startup
     *
     * @return True, while created
     */
    @Override
    public boolean onCreate() {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(AUTHORITY, "*", 1);

        return true;
    }

    /**
     * Handles requests to open a file blob
     *
     * @param uri  The URI whose file is to be opened. This value cannot be null.
     * @param mode Access mode for the file. May be "r" for read-only access, "w" for write-only access, "rw" for read and write access, or "rwt" for read and write access that truncates any existing file. This value cannot be null.
     * @return Returns a new ParcelFileDescriptor which you can use to access the file.
     * @throws FileNotFoundException Throws FileNotFoundException if there is no file associated with the given URI or the mode is invalid.
     */
    @Override
    public ParcelFileDescriptor openFile(Uri uri, String mode)
            throws FileNotFoundException {

        String LOG_TAG = CLASS_NAME + " - openFile";

        Log.v(LOG_TAG,
                "Called with uri: '" + uri + "'." + uri.getLastPathSegment());

        // Check incoming Uri against the matcher
        // If it returns 1 - then it matches the Uri defined in onCreate
        if (uriMatcher.match(uri) == 1) {// The desired file name is specified by the last segment of the
            // path

            // Take this and build the path to the file
            String fileLocation = getContext().getCacheDir() + File.separator
                    + uri.getLastPathSegment();

            // Create & return a ParcelFileDescriptor pointing to the file
            // Note: I don't care what mode they ask for - they're only getting
            // read only
            return ParcelFileDescriptor.open(new File(
                    fileLocation), ParcelFileDescriptor.MODE_READ_ONLY);

            // Otherwise unrecognised Uri
        }
        Log.v(LOG_TAG, "Unsupported uri: '" + uri + "'.");
        throw new FileNotFoundException("Unsupported uri: "
                + uri.toString());
    }

    // //////////////////////////////////
    // Not supported / used / required //
    // //////////////////////////////////

    @Override
    public int update(Uri uri, ContentValues contentvalues, String s,
                      String[] as) {
        return 0;
    }

    @Override
    public int delete(Uri uri, String s, String[] as) {
        return 0;
    }

    @Override
    public Uri insert(Uri uri, ContentValues contentvalues) {
        return null;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String s, String[] as1,
                        String s1) {
        return null;
    }
}


