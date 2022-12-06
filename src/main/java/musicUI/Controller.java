package musicUI;


import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import musicUI.model.Album;
import musicUI.model.Artist;
import musicUI.model.DataSource;
import musicUI.model.Song;

import java.io.IOException;
import java.util.Optional;

enum DialogMode {ADD, UPDATE}


public class Controller<T> {


    @FXML
    public TableView mainTable;

    @FXML
    public TableColumn columnName;

    @FXML
    private ProgressBar progressBar;

    @FXML
    private Button songsBT;

    @FXML
    private Button updateBT;

    @FXML
    private Button albumBT;

    @FXML
    public Button insertBT;

    @FXML
    public Button deleteBT;

    Artist artistID;
    Album albumId;


    @FXML
    public void listArtist() {
        columnName.setCellValueFactory(new PropertyValueFactory<>("name"));
        Task<ObservableList<Artist>> task = new getAllArtistTask();
        mainTable.itemsProperty().bind(task.valueProperty());

        progressBar.progressProperty().bind(task.progressProperty());

        progressBar.setVisible(true);

        task.setOnSucceeded(e -> {
            progressBar.setVisible(false);
            songsBT.setVisible(false);
            albumBT.setVisible(true);
            albumBT.setManaged(true);

            insertBT.setText("Insert Artist");
            updateBT.setText("Update Artist");
            deleteBT.setText("Delete Artist");

        });

        task.setOnFailed(e -> progressBar.setVisible(false));


        new Thread(task).start();
    }

    @FXML
    public void listAlbumsForArtist() {
//        final Artist artist = (Artist) mainTable.getSelectionModel().getSelectedItem();
        artistID = (Artist) mainTable.getSelectionModel().getSelectedItem();

        if (artistID == null) {
            showErrorDialog("Please select an artist");
            return;
        }
        Task<ObservableList<Album>> task = new Task<>() {
            @Override
            protected ObservableList<Album> call() {
                return FXCollections.observableArrayList(DataSource.getInstance().queryAlbumsForArtistId(artistID.getId()));
            }
        };
        mainTable.itemsProperty().bind(task.valueProperty());
        task.setOnSucceeded(e -> {
            songsBT.setVisible(true);
            albumBT.setVisible(false);
            albumBT.setManaged(false);

            insertBT.setText("Insert Album");
            updateBT.setText("Update Album");
            deleteBT.setText("Delete Album");

        });


        new Thread(task).start();
    }

    @FXML
    public void listSongsForAlbum() {
        columnName.setCellValueFactory(new PropertyValueFactory<>("title"));
        // is albumTable not artistTable
        final Album album = (Album) mainTable.getSelectionModel().getSelectedItem();
        albumId = (Album) mainTable.getSelectionModel().getSelectedItem();

        if (album == null) {
            showErrorDialog("Please select an album");
            return;
        }
        Task<ObservableList<Song>> task = new Task<>() {
            @Override
            protected ObservableList<Song> call() {
                return FXCollections.observableArrayList(DataSource.getInstance().querySongsForAlbumId(album.getId()));
            }
        };
        mainTable.itemsProperty().bind(task.valueProperty());
        task.setOnSucceeded(e -> {
            albumBT.setVisible(false);
            albumBT.setManaged(false);
            songsBT.setVisible(false);
            songsBT.setManaged(true);

            insertBT.setText("Insert Song");
            updateBT.setText("Update Song");
            deleteBT.setText("Delete Song");

        });
        new Thread(task).start();
    }


    @FXML
    public void checkArtist(String name) {
        Task<Integer> task = new Task<>() {
            @Override
            protected Integer call() {
                return DataSource.getInstance().checkArtist(name);
            }
        };


        task.setOnSucceeded(e -> {
            if (task.valueProperty().get() == -1) {
                showErrorDialog("Artist " + name + " already exists!");
                return;
            }
            insertArtist(name);
        });
        new Thread(task).start();


    }

    @FXML
    public void checkAlbum(int ArtistID, String name) {
        Task<Boolean> task = new Task<>() {
            @Override
            protected Boolean call() {
                return DataSource.getInstance().checkAlbum(ArtistID, name);
            }
        };


        task.setOnSucceeded(e -> {
            if (!task.valueProperty().get()) {
                showErrorDialog("Album " + name + " already exists!");
                return;
            }
            insertAlbum(ArtistID, name);
        });
        new Thread(task).start();


    }

    @FXML
    public void checkSong(int AlbumID, String name) {
        Task<Boolean> task = new Task<>() {
            @Override
            protected Boolean call() {
                return DataSource.getInstance().checkSong(AlbumID, name);
            }
        };


        task.setOnSucceeded(e -> {
            if (!task.valueProperty().get()) {
                showErrorDialog("Song " + name + " already exists!");
                return;
            }
            insertSong(AlbumID, name);
        });
        new Thread(task).start();


    }

    @FXML
    public void deleteArtist() {
        final Artist artist = (Artist) mainTable.getSelectionModel().getSelectedItem();

        if (artist == null) {
            showErrorDialog("Please select an artist");
            return;
        }
        Optional<ButtonType> isConfirmed=showConfirmationDialog("Are you sure you want to delete?");
        if(isConfirmed.isPresent() && isConfirmed.get()==ButtonType.OK ) {

            Task<Boolean> task = new Task<>() {
                @Override
                protected Boolean call() {
                    return DataSource.getInstance().deleteArtist(artist.getId());
                }
            };
            task.setOnSucceeded(e -> {
                mainTable.getItems().remove(artist);
                showInformationDialog("Record has been successfully deleted!");
            });
            new Thread(task).start();
        }



    }

    @FXML
    public void deleteAlbum() {
        final Album album = (Album) mainTable.getSelectionModel().getSelectedItem();

        if (album == null) {
            showErrorDialog("Please select an album");
            return;
        }

        Optional<ButtonType> isConfirmed=showConfirmationDialog("Are you sure you want to delete?");
        if(isConfirmed.isPresent() && isConfirmed.get()==ButtonType.OK ) {

            Task<Boolean> task = new Task<>() {
                @Override
                protected Boolean call() {
                    return DataSource.getInstance().deleteAlbumById(album.getId());
                }
            };

            task.setOnSucceeded(e -> {
                mainTable.getItems().remove(album);
                showInformationDialog("Record has been successfully deleted!");
            });
            new Thread(task).start();
        }

    }

    @FXML
    public void deleteSongById() {
        final Song song = (Song) mainTable.getSelectionModel().getSelectedItem();

        if (song == null) {
            showErrorDialog("Please select a song");
            return;
        }

        Optional<ButtonType> isConfirmed=showConfirmationDialog("Are you sure you want to delete?");
        if(isConfirmed.isPresent() && isConfirmed.get()==ButtonType.OK ) {

            Task<Boolean> task = new Task<>() {
                @Override
                protected Boolean call() {
                    return DataSource.getInstance().deleteSongBySongId(song.getId());
                }
            };
            task.setOnSucceeded(e -> {
                mainTable.getItems().remove(song);

                showInformationDialog("Record has been successfully deleted");
            });
            new Thread(task).start();
        }

    }


    @FXML
    public void insertArtist(String name) {
        Task<Integer> task = new Task<>() {
            @Override
            protected Integer call() {
                return DataSource.getInstance().insertInArtist(name);
            }
        };
        System.out.println(task.getValue());

        task.setOnSucceeded(e -> {
            Artist artist = new Artist(task.getValue(), name);
            mainTable.getItems().add(artist);
            mainTable.getSortOrder().add(columnName);
            showInformationDialog("Record has been successfully inserted");
        });
        task.setOnFailed(e -> showErrorDialog("Couldn't insert artist"));
        new Thread(task).start();


    }

    @FXML
    public void insertAlbum(int id, String name) {
        Task<Integer> task = new Task<>() {
            @Override
            protected Integer call() throws Exception {
                return DataSource.getInstance().insertInAlbum(id, name);
            }
        };
        System.out.println(task.getValue());

        task.setOnSucceeded(e -> {
//            listArtist();
            Album album = new Album(task.getValue(), name);
            mainTable.getItems().add(album);
            mainTable.getSortOrder().add(columnName);
            showInformationDialog("Record has been successfully inserted");
        });
        task.setOnFailed(e -> showErrorDialog("Couldn't insert album"));
        new Thread(task).start();


    }

    @FXML
    public void insertSong(int SongID, String name) {
        Task<Integer> task = new Task<>() {
            @Override
            protected Integer call() {
                return DataSource.getInstance().insertInSong(SongID, name);
            }
        };
        System.out.println(task.getValue());

        task.setOnSucceeded(e -> {
//            listArtist();
            Song song = new Song(task.getValue(), name);
            mainTable.getItems().add(song);
            mainTable.getSortOrder().add(columnName);
            showInformationDialog("Record has been successfully inserted");
        });
        task.setOnFailed(e -> showErrorDialog("Couldn't insert song"));
        new Thread(task).start();


    }

    @FXML
    public void updateArtist(String newName) {
        final Artist artist = (Artist) mainTable.getSelectionModel().getSelectedItem();

        Task<Boolean> task = new Task<>() {
            @Override
            protected Boolean call() {
                return DataSource.getInstance().updateArtist(artist.getId(), newName);
            }
        };

        task.setOnSucceeded(e -> {
            if (task.valueProperty().get()) {
                artist.setName(newName);
                mainTable.refresh();
            }
            showInformationDialog("Record has been successfully updated");
        });
        new Thread(task).start();

    }

    @FXML
    public void updateAlbum(String newName) {
        final Album album = (Album) mainTable.getSelectionModel().getSelectedItem();

        Task<Boolean> task = new Task<>() {
            @Override
            protected Boolean call() {
                return DataSource.getInstance().updateAlbum(album.getId(), newName);
            }
        };

        task.setOnSucceeded(e -> {
            if (task.valueProperty().get()) {
                album.setName(newName);
                mainTable.refresh();
            }
            showInformationDialog("Record has been successfully updated");
        });
        new Thread(task).start();

    }

    @FXML
    public void updateSong(String newName) {
        final Song song = (Song) mainTable.getSelectionModel().getSelectedItem();

        Task<Boolean> task = new Task<>() {
            @Override
            protected Boolean call() {
                return DataSource.getInstance().updateSong(song.getId(), newName);
            }
        };

        task.setOnSucceeded(e -> {
            if (task.valueProperty().get()) {
                song.setTitle(newName);
                mainTable.refresh();
            }
            showInformationDialog("Record has been successfully updated");
        });
        new Thread(task).start();

    }


    @FXML
    public void showDialogSong(ActionEvent event) {
        final Song song = (Song) mainTable.getSelectionModel().getSelectedItem();
        String dialogTitle = "";
        DialogMode mode = null;


        if (event.getSource().equals(updateBT)) {
            if (song == null) {
                showErrorDialog("Please select a song");
                return;
            }
            mode = DialogMode.UPDATE;
            dialogTitle = "Update Song";
        } else if (event.getSource().equals(insertBT)) {
            mode = DialogMode.ADD;
            dialogTitle = "Insert Song";

        }


        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle(dialogTitle);
        dialog.initOwner(mainTable.getScene().getWindow());
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("updateDialog.fxml"));
        try {
            dialog.getDialogPane().setContent(fxmlLoader.load());
        } catch (IOException e) {
            System.out.println("Couldn't load dialog " + e.getMessage());
            e.printStackTrace();
            return;
        }


        DialogController controller = fxmlLoader.getController();

        if (event.getSource().equals(updateBT))
            controller.setNameField(song.getTitle());

        dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);

        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {

            if (mode == DialogMode.UPDATE) {
                updateSong(controller.getName());
            } else if (mode == DialogMode.ADD)
                checkSong(albumId.getId(), controller.getName());
        } else {
            System.out.println("Cancel");
        }
    }

    @FXML
    public void showDialogArtist(ActionEvent event) {
        final Artist artist = (Artist) mainTable.getSelectionModel().getSelectedItem();
        String dialogTitle = "";
        DialogMode mode = null;


        if (event.getSource().equals(updateBT)) {
            if (artist == null) {
                showErrorDialog("Please select an artist");
                return;
            }
            mode = DialogMode.UPDATE;
            dialogTitle = "Update Artist";
        } else if (event.getSource().equals(insertBT)) {
            mode = DialogMode.ADD;
            dialogTitle = "Insert Artist";

        }


        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle(dialogTitle);
        dialog.initOwner(mainTable.getScene().getWindow());
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("updateDialog.fxml"));
        try {
            dialog.getDialogPane().setContent(fxmlLoader.load());
        } catch (IOException e) {
            System.out.println("Couldn't load dialog " + e.getMessage());
            e.printStackTrace();
            return;
        }


        DialogController controller = fxmlLoader.getController();

        if (event.getSource().equals(updateBT))
            controller.setNameField(artist.getName());

        dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);

        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {

            if (mode == DialogMode.UPDATE) {
                updateArtist(controller.getName());
            } else if (mode == DialogMode.ADD)
                checkArtist(controller.getName());
        } else {
            System.out.println("Cancel");
        }
    }

    @FXML
    public void showDialogAlbum(ActionEvent event) {
        Album album = (Album) mainTable.getSelectionModel().getSelectedItem();

        String dialogTitle = "";
        DialogMode mode = null;


        if (event.getSource().equals(updateBT)) {

            if (album == null) {
                showErrorDialog("Please select an album");
                return;
            }
            mode = DialogMode.UPDATE;
            dialogTitle = "Update Album";
        } else if (event.getSource().equals(insertBT)) {
            mode = DialogMode.ADD;
            dialogTitle = "Insert Album";

        }


        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle(dialogTitle);
        dialog.initOwner(mainTable.getScene().getWindow());
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("updateDialog.fxml"));
        try {
            dialog.getDialogPane().setContent(fxmlLoader.load());
        } catch (IOException e) {
            System.out.println("Couldn't load dialog " + e.getMessage());
            e.printStackTrace();
            return;
        }


        DialogController controller = fxmlLoader.getController();

        if (event.getSource().equals(updateBT))
            controller.setNameField(album.getName());

        dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);

        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {

            if (mode == DialogMode.UPDATE) {
                updateAlbum(controller.getName());
            } else if (mode == DialogMode.ADD) {
                checkAlbum(artistID.getId(), controller.getName());
            }
        } else {
            System.out.println("Cancel");
        }
    }

    @FXML
    public void Actions(ActionEvent event) {

        if (event.getSource().equals(deleteBT)) {


            if (deleteBT.getText().equals("Delete Artist")) {
                deleteArtist();
            } else if (deleteBT.getText().equals("Delete Album")) {
                deleteAlbum();
            } else if (deleteBT.getText().equals("Delete Song")) {
                deleteSongById();
            }


        } else {
            if (updateBT.getText().equals("Update Artist") || insertBT.getText().equals("Insert Artist")) {
                showDialogArtist(event);
            } else if (updateBT.getText().equals("Update Album") || insertBT.getText().equals("Insert Album")) {
                showDialogAlbum(event);
            } else if (updateBT.getText().equals("Update Song") || insertBT.getText().equals("Insert Song")) {
                showDialogSong(event);
            }
        }


    }

    private void showErrorDialog(String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(content);

        alert.showAndWait();
    }

    private void showInformationDialog(String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText(null);
        alert.setContentText(content);

        alert.showAndWait();
    }

    private Optional<ButtonType> showConfirmationDialog(String content) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Delete");
        alert.setHeaderText(null);
        alert.setContentText(content);

        return alert.showAndWait();
    }


}

class getAllArtistTask extends Task {


    @Override
    public ObservableList<Artist> call() {
        return FXCollections.observableArrayList(DataSource.getInstance().queryArtist());
    }
}

