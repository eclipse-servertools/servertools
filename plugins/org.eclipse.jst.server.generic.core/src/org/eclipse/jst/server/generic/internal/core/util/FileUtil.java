package org.eclipse.jst.server.generic.internal.core.util;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Bundle;

/**
 * 
 *
 * @author Gorkem Ercan
 */
public class FileUtil {

    public static File resolveFileFrom(Bundle bundle, String file)
    {
        try {
            URL resolvedUrl = Platform.resolve(bundle.getEntry("/"));
            if(file.startsWith("/"))
                file=file.substring(1);
            String path = resolvedUrl.getPath()+file;
            URI uri = new URI(resolvedUrl.getProtocol(), resolvedUrl.getHost(),path, resolvedUrl.getQuery());
            return new File(uri);
        } 
        catch (URISyntaxException e) {
        }
        catch (IOException e1) {
        }
        return null;
    }
}
