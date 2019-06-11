package model;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UnitTest {
    @Test
    void testChangeImageName() {
        model.FileManager fileManager = new model.FileManager("/Users/yifanm/Desktop//group_0465/phase2/testImage/sunset.jpg");
        model.LogManager logManager = new model.LogManager(System.getProperty("user.home") + "/log.txt");
        fileManager.changeImageName("lovelysunset", logManager);
        File file = fileManager.changeImageName("lovelysunset", logManager);
        assertEquals("/Users/yifanm/Desktop/group_0465/phase2/testImage/lovelysunset.jpg", file.getAbsolutePath());
    }

    @Test
    void testAddTagToImage(){
        model.FileManager fileManager = new model.FileManager("/Users/yifanm/Desktop/group_0465/phase2/testImage/sunset.jpg");
        model.LogManager logManager = new model.LogManager(System.getProperty("user.home") + "/log.txt");
        String tag = "landscape";
        File file = fileManager.addTagToImage(tag, logManager);
        assertEquals("/Users/yifanm/Desktop/group_0465/phase2/testImage/sunset @landscape.jpg", file.getAbsolutePath());
    }

    @Test
    void testDeleteTagToImage() {
        model.FileManager fileManager = new model.FileManager(
                                                    "/Users/yifanm/Desktop/group_0465/phase2/testImage/sunset @landscape.jpg");
        model.LogManager logManager = new model.LogManager(System.getProperty("user.home") + "/log.txt");
        String tag = "landscape";
        File file = fileManager.deleteTagToImage(tag, logManager);
        assertEquals("/Users/yifanm/Desktop/group_0465/phase2/testImage/sunset.jpg", file.getAbsolutePath());
    }

    @Test
    void testGetImageTags() {
        ArrayList<String> tags =
                FileManager.getImageTags("/Users/yifanm/Desktop/group_0465/phase2/testImage/sunset @landscape @lovely.jpg");
        String[] tag_list = tags.toArray(new String[tags.size()]);
        assertEquals("[landscape, lovely]", Arrays.toString(tag_list));
    }

    @Test
    void testContainsTag() {
        ArrayList<String> tags = new ArrayList<>();
        tags.add("landscape");
        assertTrue(FileManager.containsTag("/Users/yifanm/Desktop/group_0465/phase2/testImage/sunset @landscape.jpg", tags));
    }

    @Test
    void testGetImageHistory() {
        model.FileManager fileManager = new model.FileManager("/Users/yifanm/Desktop/group_0465/phase2/testImage/sunset.jpg");
        model.LogManager logManager = new model.LogManager(System.getProperty("user.home") + "/log.txt");
        fileManager.changeImageName("lovelysunset", logManager);
        ArrayList<String> history = fileManager.getImageHistory();
        String[] history_list = history.toArray(new String[history.size()]);
        assertEquals("[lovelysunset, sunset]", Arrays.toString(history_list));
    }

    @Test
    void testMoveImage() throws IOException {
        FileManager fileManager = new FileManager("/Users/yifanm/Desktop/group_0465/phase2/testImage/lovelysunset.jpg");
        File file = fileManager.moveImage("/Users/yifanm/Desktop/group_0465/phase2/lovelysunset.jpg");
        assertTrue(file.exists());
        assertEquals("/Users/yifanm/Desktop/group_0465/phase2/lovelysunset.jpg", file.getAbsolutePath());
    }

    @Test
    void testGetInfo() {
        TagManager tagManager = new TagManager("group_0465/phase2/testImage/sunset.jpg");
        tagManager.addInfo("landscape");
        tagManager.deleteTag("landscape");
        ArrayList<String> info = tagManager.getInfo();
        String[] info_list = info.toArray(new String[info.size()]);
        assertEquals("[]", Arrays.toString(info_list));
    }
}
