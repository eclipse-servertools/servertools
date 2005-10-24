package org.eclipse.jst.server.generic.core.internal.publishers;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.CRC32;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;

/**
 * Packages resources to a .zip file
 */
public class ModulePackager {
	private ZipOutputStream outputStream;
	private StringBuffer manifestContents;

	private boolean useCompression = true;

	/**
	 * Create an instance of this class.
	 * 
	 * @param filename java.lang.String
	 * @param compress boolean
	 * @exception java.io.IOException
	 */
	public ModulePackager(String filename, boolean compress) throws IOException {
		Path directoryPath = new Path(filename);
		directoryPath = (Path) directoryPath.removeLastSegments(1);
		File newZipFile = new File(directoryPath.toString());
		newZipFile.mkdirs();
		outputStream = new ZipOutputStream(new FileOutputStream(filename));
		useCompression = compress;
	}

	/**
	 * Do all required cleanup now that we're finished with the currently-open .zip
	 * 
	 * @exception java.io.IOException
	 */
	public void finished() throws IOException {
		outputStream.close();
	}

	/**
	 * Create a new ZipEntry with the passed pathname and contents, and write it to the current
	 * archive
	 * 
	 * @param pathname
	 *            java.lang.String
	 * @param contents
	 *            byte[]
	 * @exception java.io.IOException
	 */
	protected void write(String pathname, byte[] contents) throws IOException {
		ZipEntry newEntry = new ZipEntry(pathname);

		// if the contents are being compressed then we get the below for free.
		if (!useCompression) {
			newEntry.setMethod(ZipEntry.STORED);
			newEntry.setSize(contents.length);
			CRC32 checksumCalculator = new CRC32();
			checksumCalculator.update(contents);
			newEntry.setCrc(checksumCalculator.getValue());
		}

		outputStream.putNextEntry(newEntry);
		outputStream.write(contents);
		outputStream.closeEntry();
	}

	public void writeFolder(String destinationPath) throws IOException {
		if (!destinationPath.endsWith("/"))
			destinationPath = destinationPath + '/';
		ZipEntry newEntry = new ZipEntry(destinationPath);
		outputStream.putNextEntry(newEntry);
		outputStream.closeEntry();
	}

	/**
	 * Write the passed resource to the current archive
	 * 
	 * @param resource
	 *            org.eclipse.core.resources.IFile
	 * @param destinationPath
	 *            java.lang.String
	 * @exception java.io.IOException
	 * @exception org.eclipse.core.runtime.CoreException
	 */
	public void write(IFile resource, String destinationPath) throws IOException, CoreException {
		InputStream contentStream = null;
		try {
			contentStream = resource.getContents(false);
			write(contentStream, destinationPath);
		} finally {
			if (contentStream != null)
				contentStream.close();
		}
	}

	public void write(InputStream contentStream, String destinationPath) throws IOException, CoreException {
		ByteArrayOutputStream output = null;

		try {
			output = new ByteArrayOutputStream();
			int chunkSize = contentStream.available();
			byte[] readBuffer = new byte[chunkSize];
			int n = contentStream.read(readBuffer);

			while (n > 0) {
				output.write(readBuffer);
				n = contentStream.read(readBuffer);
			}
		} finally {
			if (output != null)
				output.close();
		}

		write(destinationPath, output.toByteArray());
	}

}
