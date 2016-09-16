/*******************************************************************************
 * Copyright 2007(c) Genome Quebec. All rights reserved.
 * <p>
 * This file is part of GenoByte.
 * <p>
 * GenoByte is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 * <p>
 * GenoByte is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *******************************************************************************/
package org.obiba.genobyte.cli;

import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.ParseException;
import org.obiba.bitwise.util.WildcardFileFilter;

import java.io.File;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * Helps create commands that require loading a single file from disk.
 * <p/>
 * Only one instance of this command may be registered to a {@link BitwiseCli}. For every
 * file type that needs to be handled, an implementation of {@link FileTypeLoader} should be
 * added to the <tt>LoadFileCommand</tt> instance using {@link LoadFileCommand#addFileTypeLoader(org.obiba.bitwise.genotyping.cli.LoadFileCommand.FileTypeLoader)}.
 * {@link FileTypeLoader} instance are identified using a long or short version of the file type name.
 * <p/>
 * The command format is "<tt>load &lt;type&gt; &lt;pattern&gt;</tt>" where
 * <ul>
 * <li><tt>type</tt> is either the long or short name of the file type</li>
 * <li><tt>file</tt> is the filename pattern of the file(s) to be loaded.</li>
 * </ul>
 */
public class LoadFileCommand implements CliCommand {

  /**
   * Registered FileTypeLoader instances
   */
  private List<FileTypeLoader> loaders_ = new LinkedList<FileTypeLoader>();

  public boolean requiresOpenStore() {
    return true;
  }

  /**
   * Executes the file loading procedure. The type and filename are extracted from the <tt>Option</tt> instance.
   * The method {@link FileTypeLoader#loadFile(CliContext, File)} is called on the appropriate instance. If no such instance
   * exists, an error message is printed on the {@link CliContext#getOutput()} stream.
   */
  public boolean execute(Option opt, CliContext context) throws ParseException {
    String args[] = opt.getValues();
    if (args == null || args.length != 2) {
      context.getOutput()
          .println("Missing argument to load command. Please specify the type of file and the filename to load.");
      return false;
    }
    String type = args[0];
    String filename = args[1];

    FileTypeLoader l = null;
    for (FileTypeLoader loader : loaders_) {
      if (loader.getFileType().equalsIgnoreCase(type) ||
          (type.length() == 1 && loader.getShortFileType() == type.charAt(0))) {
        l = loader;
        break;
      }
    }

    if (l == null) {
      context.getOutput().println("There is no loader registered for the file type [" + type + "] specified.");
      return false;
    }

    if (l.requiresOpenStore() && context.getStore() == null) {
      context.getOutput().println("Open a store before loading a file of type [" + l.getFileType() + "]");
      return false;
    }

    File files[] = WildcardFileFilter.listFiles(new File("."), filename);
    if (files == null || files.length == 0) {
      context.getOutput().println("No match for filename [" + filename + "].");
      return false;
    }

    for (int i = 0; i < files.length; i++) {
      File f = files[i];
      if (f.canRead() == false) {
        context.getOutput().println("The file [" + f.getName() + "] cannot be read.");
        return false;
      }
    }

    if (files.length > 1 && l.allowsMultipleFile() == false) {
      context.getOutput().println(
          "Cannot load multiple files of type [" + l.getFileType() + "]. Please specify only one file to load.");
      return false;
    }

    if (files.length > 1) {
      context.getOutput().println("Loading files " + Arrays.toString(files));
    } else {
      context.getOutput().println("Loading file [" + files[0].getName() + "]");
    }
    l.loadFiles(context, files);

    return false;
  }

  public Option getOption() {
    StringBuilder sb = new StringBuilder();
    for (FileTypeLoader loader : loaders_) {
      if (sb.length() > 0) sb.append(", ");
      sb.append(loader.getFileType()).append(" (").append(loader.getShortFileType()).append(")");
    }
    return OptionBuilder.withDescription("loads a file. Available types are [" + sb.toString() + "]")
        .withLongOpt("load").hasArgs(2).withArgName("type> <file").create('l');
  }

  /**
   * Adds an instance of {@link FileTypeLoader} that will may load a certain file type.
   *
   * @param loader the instance to be registered
   */
  public void addFileTypeLoader(FileTypeLoader loader) {
    loaders_.add(loader);
  }

  /**
   * Returns true is the command has an instance of {@link FileTypeLoader} that uses the specified short name as its file type.
   * <p/>
   * If any registered loader's method {@link FileTypeLoader#getShortFileType()} returns <tt>s</tt>, this method returns true. Otherwise, false is returned.
   *
   * @param s the short name to check.
   * @return true if a registered loader instance uses the <tt>s</tt> as its short name.
   */
  public boolean hasFileTypeLoader(char s) {
    for (FileTypeLoader loader : loaders_) {
      if (loader.getShortFileType() == s) return true;
    }
    return false;
  }

  /**
   * Handles the loading of a certain file type. Implementations of this interface should
   * provide a unique name and short
   */
  public interface FileTypeLoader {

    /**
     * Returns the name of the file type handled by this implementation.
     * <p/>
     * The <tt>String</tt> returned by this method will be used by the CLI user to
     * invoke the proper instance to use for loading a file. For example, if this method returns
     * "samples", the user will invoke this loader by typing "load samples <filename>" on the CLI prompt.
     *
     * @return the long version of the file type name
     */
    public String getFileType();

    /**
     * Returns a short version of the file type name.
     * <p/>
     * The character returned by this method will allow the user to invoke the loader
     * in exactly the same way the longer version would.
     *
     * @return the short version of the file type name
     */
    public char getShortFileType();

    /**
     * Executes the actual loading of a file.
     *
     * @param context the context of the CLI
     * @param files   the file(s) to be loaded
     */
    public void loadFiles(CliContext context, File... files);

    /**
     * Returns true if the loader requires that a store is opened before processing the file(s).
     *
     * @return true if a store is required
     */
    public boolean requiresOpenStore();

    /**
     * Returns true if the loader is able to process multiple files of the same type in one run.
     *
     * @return true if the loader may load multiple files in one execution.
     */
    public boolean allowsMultipleFile();

  }

}
