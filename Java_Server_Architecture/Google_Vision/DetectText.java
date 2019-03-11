/**
 * (C) Copyright Aditya Prerepa
 * Code was created for MenloHacks.
 */


import com.google.cloud.vision.v1.AnnotateImageRequest;
import com.google.cloud.vision.v1.AnnotateImageResponse;
import com.google.cloud.vision.v1.BatchAnnotateImagesResponse;
import com.google.cloud.vision.v1.EntityAnnotation;
import com.google.cloud.vision.v1.Feature;
import com.google.cloud.vision.v1.Feature.Type;
import com.google.cloud.vision.v1.Image;
import com.google.cloud.vision.v1.ImageAnnotatorClient;

import com.google.protobuf.ByteString;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * This class creates an object that has
 * a method that can detect text in a given image.
 * @apiNote Google Vision
 * @author Aditya Prerepa
 */
public class DetectText {

    /**
     * Pre - define image path, make it a global variable
     * limited to the scope of the class.
     */
    private File imagePath;
    private static String JSONResults_Positions;

    /**
     * Default constructor.
     * @param imagePath
     */
    public DetectText(File imagePath) {
        this.imagePath = imagePath;
    }

    /**
     * Detects entities, sentiment, and syntax in a document using the Vision API.
     *
     * @throws Exception on errors while closing the client.
     * @throws IOException on Input/Output errors.
     */
    public String mainMethod() {
        String path = imagePath.toString();
        String license = null;
        try {
            license = detectText(path, System.out);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return license;
    }

    /**
     * Detects text in the specified image.
     * @param filePath The path to the file to detect text in.
     * @param out A {@link PrintStream} to write the detected text to.
     * @throws Exception on errors while closing the client.
     * @throws IOException on Input/Output errors.
     */
    private static String detectText(String filePath, PrintStream out) throws Exception, IOException {
        String retunString = null;
        List<String> allText = new ArrayList<>();
        List<AnnotateImageRequest> requests = new ArrayList<>();
        ByteString imgBytes = ByteString.readFrom(new FileInputStream(filePath));

        Image img = Image.newBuilder().setContent(imgBytes).build();
        Feature feat = Feature.newBuilder().setType(Type.TEXT_DETECTION).build();
        AnnotateImageRequest request =
                AnnotateImageRequest.newBuilder().addFeatures(feat).setImage(img).build();
        requests.add(request);

        try (ImageAnnotatorClient client = ImageAnnotatorClient.create()) {
            BatchAnnotateImagesResponse response = client.batchAnnotateImages(requests);
            List<AnnotateImageResponse> responses = response.getResponsesList();

            for (AnnotateImageResponse res : responses) {
                if (res.hasError()) {
                    out.printf("Error: %s\n", res.getError().getMessage());
                    break;
                }

                // For full list of available annotations, see http://g.co/cloud/vision/docs
//                for (EntityAnnotation annotation : res.getTextAnnotationsList()) {
//                    retunString = annotation.getDescription();
//                    allText.add(retunString);
//                    JSONResults_Positions = annotation.getDescription();
//                    System.out.println(annotation.getDescription());
//                    List<String> plates = Arrays.asList(retunString.split("\\s+"));
//
//
//                    //out.printf("Text: %s\n", annotation.getDescription());
//                    //out.printf("Position : %s\n", annotation.getBoundingPoly());
//                    //System.out.println(annotation.getBoundingPoly());
//
//                    //out.println(annotation.getDescription());
//                    /**
//                    out.printf("Text: %s\n", annotation.getDescription());
//                    out.printf("Position : %s\n", annotation.getBoundingPoly());
//                    out.printf("Confidence %f\n", annotation.getConfidence());
//                     */
//                }

                for (EntityAnnotation annotation : res.getTextAnnotationsList()) {
                    retunString = annotation.getDescription();
                    allText.add(retunString);
                    JSONResults_Positions = annotation.getDescription();
                    System.out.println(annotation.getDescription());
                    List<String> plates = Arrays.asList(retunString.split("\\s+"));


                    //out.printf("Text: %s\n", annotation.getDescription());
                    //out.printf("Position : %s\n", annotation.getBoundingPoly());
                    //System.out.println(annotation.getBoundingPoly());

                    //out.println(annotation.getDescription());
                    /**
                     out.printf("Text: %s\n", annotation.getDescription());
                     out.printf("Position : %s\n", annotation.getBoundingPoly());
                     out.printf("Confidence %f\n", annotation.getConfidence());
                     */
                    break;
                }

            }
        }

        int x = 0;
        for (String res : allText) {
            //System.out.println(res);
            x++;
            if (x == 1)
                break;
        }
        String result = String.join(" ", allText);
        //System.out.println(result);
        return result.replaceAll("[\\n\\r]+", " ");
    }

    private static void getJSONObjects() {
        //JSONObject obj = new JSONObject(JSONResults_Positions);


    }
}
