package net.nandgr.cba.decompile;

import com.strobel.decompiler.PlainTextOutput;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import net.nandgr.cba.cli.CliHelper;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ZipEntryDecompiler implements Decompiler {

  private static final Logger logger = LoggerFactory.getLogger(ZipEntryDecompiler.class);
  private static final String CLASS_SUFFIX = ".class";
  private static final String DECOMPILED_DIR = "decompiled/";

  @Override
  public void decompile(InputStream inputStream, String entryName) throws IOException {
    logger.debug("Decompiling... {}", entryName);
    File tempFile = createTempFile(entryName, inputStream);
    String decompiledFileName = getDecompiledFileName(entryName);
    File decompiledFile = new File(decompiledFileName);
    decompiledFile.getParentFile().mkdirs();
    PrintWriter pw = new PrintWriter(decompiledFile);
    try {
      com.strobel.decompiler.Decompiler.decompile(tempFile.getAbsolutePath(), new PlainTextOutput(pw));
    } catch (Exception e) {
      logger.info("Error while decompiling {}. " , entryName);
      throw e;
    }
    pw.flush();
//    tempFile.delete();
  }

  private static File createTempFile(String entryName, InputStream inputStream) throws IOException {
    byte[] inputStreamBytes = IOUtils.toByteArray(inputStream);
    InputStream byteArrayInputStream = new ByteArrayInputStream(inputStreamBytes);

    String prefix = entryName.replaceAll(File.separator, ".");
    final File tempFile = File.createTempFile(prefix, ".class");
    FileOutputStream outputStream = new FileOutputStream(tempFile);
    int copiedBytes = IOUtils.copy(byteArrayInputStream, outputStream);
    logger.debug("Copied {} bytes to a temp file.", copiedBytes);
    return tempFile;
  }

  private static String getDecompiledFileName(String entryName) {
    String outputDir = CliHelper.getOutputDir();
    if (!outputDir.endsWith(File.separator)) {
      outputDir += File.separator;
    }
    outputDir += DECOMPILED_DIR;
    String zipEntryName = entryName;
    if (zipEntryName.endsWith(CLASS_SUFFIX)) {
      zipEntryName = zipEntryName.substring(0, zipEntryName.length() - CLASS_SUFFIX.length());
    }
    return outputDir + zipEntryName + ".java";
  }
}
