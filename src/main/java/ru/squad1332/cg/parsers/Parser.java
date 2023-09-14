package ru.squad1332.cg.parsers;

import ru.squad1332.cg.entities.Picture;
import ru.squad1332.cg.entities.PicturePNM;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;

public interface Parser {
    PicturePNM parse(String path) throws IOException;
}
