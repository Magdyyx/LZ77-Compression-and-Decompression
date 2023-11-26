import algorithms.Algorithm;
import algorithms.LZWAlgorithm;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.input.MouseEvent;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;

import javafx.stage.FileChooser;
import javafx.stage.Stage;


public class CrunchByteController implements Initializable {
   FileChooser fileChooser = new FileChooser();
   @FXML
   private ChoiceBox<String> algorithmChoice = new ChoiceBox<>();
   private String[] algorithms = {"LZ77", "LZW", "Modified Huffman"};
   private Stage stage;
   private Scene scene;
   private Parent root;
   @FXML
   private Button actionBtn;


   @Override
   public void initialize(URL location, ResourceBundle resources) {
      fileChooser.setInitialDirectory(new File("D:\\Study\\Level 3\\Information Theory\\Assignments\\CrunchByte"));
      algorithmChoice.getItems().addAll(algorithms);
   }

   @FXML
   public void openCompressionWindow(MouseEvent event) throws IOException {
      Parent root = FXMLLoader.load(getClass().getResource("Compression.fxml"));
      stage = (Stage)((Node)event.getSource()).getScene().getWindow();
      stage.close();
      scene = new Scene(root);
      stage.setScene(scene);
      stage.setTitle("File Compression");
      stage.show();

   }

   @FXML
   public void openDecompressionWindow(MouseEvent event) throws IOException {
      Parent root = FXMLLoader.load(getClass().getResource("Decompression.fxml"));
      stage = (Stage)((Node)event.getSource()).getScene().getWindow();
      stage.close();
      scene = new Scene(root);
      stage.setScene(scene);
      stage.setTitle("File Decompression");
      stage.show();
   }


   @FXML
   void goBackToWelcome(MouseEvent event) throws IOException {
      Parent root = FXMLLoader.load(getClass().getResource("Welcome.fxml"));
      stage = (Stage)((Node)event.getSource()).getScene().getWindow();
      stage.close();
      scene = new Scene(root);
      stage.setScene(scene);
      stage.setTitle("Crunch Byte");
      stage.show();
   }

   @FXML
   void getFile(MouseEvent event) {
      File file = fileChooser.showOpenDialog(new Stage());
      String algorithm = algorithmChoice.getValue();
      String action = actionBtn.getText();
      Alert alert;
      if(performAlgorithm(file, algorithm, action)){
         alert = new Alert(Alert.AlertType.CONFIRMATION);
         alert.setTitle("Information Dialog!!");
         alert.setHeaderText("Action Done");
         alert.showAndWait();
      } else {
         alert = new Alert(Alert.AlertType.ERROR);
         alert.setTitle("Error Dialog!!");
         alert.setHeaderText("Action Failed");
         alert.showAndWait();
      }
   }



   private boolean performAlgorithm(File inputFile, String algorithmName, String action) {
      Algorithm algorithm = algorithmFactory(algorithmName);
      boolean actionDone = false;
      if (action.equals("Compress")){
         actionDone = algorithm.compress(inputFile);
      } else {
         actionDone = algorithm.decompress(inputFile);
      }
      return actionDone;
   }


   private Algorithm algorithmFactory(String algorithmName) {
      Algorithm algorithm = null;
      if(algorithmName.equals("LZW")) {
         algorithm = (Algorithm) new LZWAlgorithm();
      } else if (algorithmName.equals("LZ77")) {
         // create new pbject of LZ77
      } else if (algorithmName.equals("Modified Huffman")) {
         //
      }
      return algorithm;
   }
}

