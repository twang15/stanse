package cz.muni.stanse.checker;

import java.util.LinkedList;
import java.io.File;

public abstract class CheckerCreator {

    // public section

    public abstract String getCheckerName();
    public abstract String getCheckerCreationInfo();
    public abstract LinkedList<String> getDataFilesExtensions();
    public abstract Checker create(final LinkedList<File> args)throws Exception;
}