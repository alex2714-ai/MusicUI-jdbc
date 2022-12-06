package musicUI.model;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DataSource {
    public static final String DB_NAME = "music.db";
    public static final Path path= Paths.get(DB_NAME);

    public static final String CONNECTION_STRING = "jdbc:sqlite:"+path.toAbsolutePath();


    public static final String TABLE_SONGS = "songs";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_TRACK = "track";
    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_ALBUM = "album";


    public static final String TABLE_ALBUMS = "albums";
    public static final String COLUMN_ALBUM_ID = "_id";
    public static final String COLUMN_ALBUM_NAME = "name";
    public static final String COLUMN_ALBUM_ARTIST = "artist";


    public static final String TABLE_ARTIST = "artists";
    public static final String COLUMN_ARTIST_ID = "_id";
    public static final String COLUMN_ARTIST_NAME = "name";
    public static final int INDEX_ARTISTS_COLUMN_ID = 1;
    public static final int INDEX_ARTISTS_COLUMN_NAME = 2;


    public static final String Query_All_Artist = "Select * from " + TABLE_ARTIST + " order by " + COLUMN_ARTIST_NAME + " collate nocase ";

    public static final String QUERY_ALBUMS_BY_ARTIST_ID = "SELECT * FROM " + TABLE_ALBUMS +
            " WHERE " + COLUMN_ALBUM_ARTIST + " = ? ORDER BY " + COLUMN_ALBUM_NAME + " COLLATE NOCASE";

    public static final String QUERY_SONGS_BY_ALBUM_ID = "SELECT * FROM " + TABLE_SONGS +
            " WHERE " + COLUMN_ALBUM + " = ? ORDER BY " + COLUMN_TRACK + " COLLATE NOCASE";


    public static final String INSERT_ARTIST = "INSERT INTO " + TABLE_ARTIST + '(' + COLUMN_ARTIST_NAME + ") VALUES(?)";
    public static final String INSERT_ALBUM = "INSERT INTO " + TABLE_ALBUMS + '(' + COLUMN_ALBUM_NAME + ", " + COLUMN_ALBUM_ARTIST + ") VALUES(?,? )";
    public static final String INSERT_SONG = "INSERT INTO " + TABLE_SONGS + '(' + COLUMN_TRACK + ", " + COLUMN_TITLE + ", " + COLUMN_ALBUM + ") VALUES(?,?,?)";

    public static final String CHECK_IF_ARTIST_EXIST_QUERY = "SELECT " + COLUMN_ARTIST_ID + " FROM " + TABLE_ARTIST +
            " WHERE " + COLUMN_ARTIST_NAME + " = ?";
    public static final String CHECK_IF_ALBUM_EXIST_QUERY = "SELECT " + COLUMN_ALBUM_ID + " FROM " + TABLE_ALBUMS +
            " WHERE " + COLUMN_ALBUM_NAME + " = ?" + " AND " + COLUMN_ALBUM_ARTIST + " = ?";
    public static final String CHECK_IF_SONG_EXIST_QUERY = "SELECT " + COLUMN_ID + " FROM " + TABLE_SONGS +
            " WHERE " + COLUMN_TITLE + " = ?" + " AND " + COLUMN_ALBUM + " = ?";

    public static final String QUERY_MAX_TRACK = "SELECT MAX ( " + COLUMN_TRACK + " ) " + " FROM " + TABLE_SONGS + " WHERE " + COLUMN_ALBUM + " = ?";

    public static final String UPDATE_ARTIST_NAME = "UPDATE " + TABLE_ARTIST + " SET " +
            COLUMN_ARTIST_NAME + " = ? WHERE " + COLUMN_ARTIST_ID + " = ?";
    public static final String UPDATE_ALBUM_NAME = "UPDATE " + TABLE_ALBUMS + " SET " +
            COLUMN_ALBUM_NAME + " = ? WHERE " + COLUMN_ALBUM_ID + " = ?";
    public static final String UPDATE_SONG_NAME = "UPDATE " + TABLE_SONGS + " SET " +
            COLUMN_TITLE + " = ? WHERE " + COLUMN_ID + " = ?";

    public static final String DELETE_ARTIST_BY_ID = "DELETE FROM " + TABLE_ARTIST + " WHERE " + COLUMN_ARTIST_ID + " = ?";
    public static final String DELETE_ALBUMS_BY_ARTIST_ID = "DELETE FROM " + TABLE_ALBUMS + " WHERE " + COLUMN_ALBUM_ARTIST + " = ?";
    public static final String DELETE_SONGS_BY_ARTIST_ID = "DELETE FROM " + TABLE_SONGS +
            " WHERE " + TABLE_SONGS + "." + COLUMN_ID + " IN " + " ( " + " SELECT " + TABLE_SONGS + "." + COLUMN_ID +
            " FROM " + TABLE_SONGS + " INNER JOIN " + TABLE_ALBUMS +
            " ON " + TABLE_SONGS + "." + COLUMN_ALBUM + " = " + TABLE_ALBUMS + "." + COLUMN_ALBUM_ID + " INNER JOIN " + TABLE_ARTIST +
            " ON " + TABLE_ALBUMS + "." + COLUMN_ALBUM_ARTIST + " = " + TABLE_ARTIST + "." + COLUMN_ARTIST_ID + " WHERE " +
            TABLE_ARTIST + "." + COLUMN_ARTIST_ID + " = ?" + " )";


    public static final String DELETE_ALBUM_BY_ID = "DELETE FROM " + TABLE_ALBUMS + " WHERE " + COLUMN_ALBUM_ID + " = ?";
    public static final String DELETE_SONGS_BY_ALBUM_ID = "DELETE FROM " + TABLE_SONGS + " WHERE " + COLUMN_ALBUM + " = ?";

    public static final String DELETE_SONG = "DELETE FROM " + TABLE_SONGS + " WHERE " + COLUMN_ID + " = ?";


    private Connection conn;


    private PreparedStatement insertArtist;
    private PreparedStatement insertAlbum;
    private PreparedStatement insertSong;

    private PreparedStatement queryMaxTrack;
    private PreparedStatement queryALLArtist;
    private PreparedStatement queryArtist;
    private PreparedStatement queryAlbum;
    private PreparedStatement querySong;
    private PreparedStatement queryAlbumByArtistId;
    private PreparedStatement querySongsByAlbumId;
    private PreparedStatement updateArtistName;
    private PreparedStatement updateAlbumName;
    private PreparedStatement updateSongName;
    private PreparedStatement deleteArtist;
    private PreparedStatement deleteAlbums;
    private PreparedStatement deleteSongs;

    private PreparedStatement deleteAlbumByID;
    private PreparedStatement deleteSongByAlbumID;

    private PreparedStatement deleteSongBySongID;

    private static final DataSource instance = new DataSource();

    private DataSource() {

    }

    public static DataSource getInstance() {
        return instance;
    }

    public boolean openConnection() {
        try {
            conn = DriverManager.getConnection(CONNECTION_STRING);

            queryMaxTrack = conn.prepareStatement(QUERY_MAX_TRACK);
            queryALLArtist = conn.prepareStatement(Query_All_Artist);
            queryAlbumByArtistId = conn.prepareStatement(QUERY_ALBUMS_BY_ARTIST_ID);
            querySongsByAlbumId = conn.prepareStatement(QUERY_SONGS_BY_ALBUM_ID);

            queryArtist = conn.prepareStatement(CHECK_IF_ARTIST_EXIST_QUERY);
            queryAlbum = conn.prepareStatement(CHECK_IF_ALBUM_EXIST_QUERY);
            querySong = conn.prepareStatement(CHECK_IF_SONG_EXIST_QUERY);

            insertArtist = conn.prepareStatement(INSERT_ARTIST, Statement.RETURN_GENERATED_KEYS);
            insertAlbum = conn.prepareStatement(INSERT_ALBUM, Statement.RETURN_GENERATED_KEYS);
            insertSong = conn.prepareStatement(INSERT_SONG, Statement.RETURN_GENERATED_KEYS);

            updateArtistName = conn.prepareStatement(UPDATE_ARTIST_NAME);
            updateAlbumName = conn.prepareStatement(UPDATE_ALBUM_NAME);
            updateSongName = conn.prepareStatement(UPDATE_SONG_NAME);


            //delete all
            deleteArtist = conn.prepareStatement(DELETE_ARTIST_BY_ID);
            deleteAlbums = conn.prepareStatement(DELETE_ALBUMS_BY_ARTIST_ID);
            deleteSongs = conn.prepareStatement(DELETE_SONGS_BY_ARTIST_ID);

            deleteAlbumByID = conn.prepareStatement(DELETE_ALBUM_BY_ID);
            deleteSongByAlbumID = conn.prepareStatement(DELETE_SONGS_BY_ALBUM_ID);

            deleteSongBySongID = conn.prepareStatement(DELETE_SONG);

            return true;
        } catch (SQLException e) {
            System.out.println("Couldn't connect to database " + e.getMessage());
            e.printStackTrace();
            return false;
        }

    }


    public void closeConnection() {
        try {

            if (insertArtist != null)
                insertArtist.close();
            if (insertAlbum != null)
                insertAlbum.close();
            if (insertSong != null)
                insertSong.close();
            if (queryArtist != null)
                queryArtist.close();
            if (queryAlbum != null)
                queryAlbum.close();
            if (queryALLArtist != null)
                queryALLArtist.close();
            if (queryAlbumByArtistId != null)
                queryAlbumByArtistId.close();
            if (updateArtistName != null)
                updateArtistName.close();
            if (querySongsByAlbumId != null)
                querySongsByAlbumId.close();
            if (deleteArtist != null)
                deleteArtist.close();
            if (deleteAlbums != null)
                deleteAlbums.close();
            if (deleteSongs != null)
                deleteSongs.close();
            if (deleteAlbumByID != null)
                deleteAlbumByID.close();
            if (deleteSongByAlbumID != null)
                deleteSongByAlbumID.close();
            if (deleteSongBySongID != null)
                deleteSongBySongID.close();
            if (queryMaxTrack != null)
                queryMaxTrack.close();
            if (conn != null)
                conn.close();
        } catch (SQLException e) {
            System.out.println("Couldn't connect to database " + e.getMessage());
            e.printStackTrace();

        }
    }

    private int queryMaxTrack(int albumID) {



        try {

            queryMaxTrack.setInt(1, albumID);
            ResultSet resultSet = queryMaxTrack.executeQuery();

            if (resultSet.next())
                return resultSet.getInt(1);
            else
                return 0;

        } catch (SQLException e) {
            System.out.println("Error track number");
            return -1;
        }

    }

    public List<Artist> queryArtist() {

        try (

                ResultSet results = queryALLArtist.executeQuery()) {

            List<Artist> artists = new ArrayList<>();
            while (results.next()) {
                Artist artist = new Artist();
                artist.setId(results.getInt(INDEX_ARTISTS_COLUMN_ID));
                artist.setName(results.getString(INDEX_ARTISTS_COLUMN_NAME));
                artists.add(artist);
            }
            return artists;
        } catch (SQLException e) {
            System.out.println("Query artist failed " + e.getMessage());
            e.printStackTrace();
            return null;
        }


    }

    public List<Album> queryAlbumsForArtistId(int id) {

        try {
            queryAlbumByArtistId.setInt(1, id);
            ResultSet results = queryAlbumByArtistId.executeQuery();

            List<Album> albums = new ArrayList<>();
            while (results.next()) {
                Album album = new Album();
                album.setId(results.getInt(1));
                album.setName(results.getString(2));
                album.setArtistId(id);
                albums.add(album);
            }

            return albums;
        } catch (SQLException e) {
            System.out.println("Query failed: " + e.getMessage());
            return null;
        }

    }

    public List<Song> querySongsForAlbumId(int id) {
        try {
            querySongsByAlbumId.setInt(1, id);
            ResultSet results = querySongsByAlbumId.executeQuery();

            List<Song> songs = new ArrayList<>();
            while (results.next()) {
                Song song = new Song();
                song.setId(results.getInt(1));
                song.setTrack(results.getInt(2));
                song.setTitle(results.getString(3));
                song.setAlbumId(id);
                songs.add(song);
            }

            return songs;
        } catch (SQLException e) {
            System.out.println("Query failed: " + e.getMessage());
            return null;
        }
    }

    public int checkArtist(String name) {
        try {

            queryArtist.setString(1, name);
            ResultSet resultSet = queryArtist.executeQuery();

            Artist artist = new Artist();
            if (resultSet.next()) {
                artist.setId(resultSet.getInt(1));
            }
            return artist.getId();

        } catch (SQLException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
            return -1;
        }


    }

    public boolean checkAlbum(int id, String name) {
        try {

            queryAlbum.setString(1, name);
            queryAlbum.setInt(2, id);
            ResultSet resultSet = queryAlbum.executeQuery();

            if (resultSet.next()) {
                return false;
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
            return false;
        }
        return true;

    }

    public boolean checkSong(int id, String name) {
        try {

            querySong.setString(1, name);
            querySong.setInt(2, id);

            ResultSet resultSet = querySong.executeQuery();

            if (resultSet.next()) {
                return false;
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
            return false;
        }
        return true;

    }

    public boolean updateArtist(int id, String newName) {
        try {
            updateArtistName.setString(1, newName);
            updateArtistName.setInt(2, id);
            int affectedRecords = updateArtistName.executeUpdate();
            return affectedRecords == 1;

        } catch (SQLException e) {
            System.out.println("Update artist failed " + e.getMessage());
            return false;
        }
    }

    public boolean updateAlbum(int id, String newName) {
        try {
            updateAlbumName.setString(1, newName);
            updateAlbumName.setInt(2, id);
            int affectedRecords = updateAlbumName.executeUpdate();
            return affectedRecords == 1;

        } catch (SQLException e) {
            System.out.println("Update artist failed " + e.getMessage());
            return false;
        }
    }

    public boolean updateSong(int id, String newName) {
        try {
            updateSongName.setString(1, newName);
            updateSongName.setInt(2, id);
            int affectedRecords = updateSongName.executeUpdate();
            return affectedRecords == 1;

        } catch (SQLException e) {
            System.out.println("Update artist failed " + e.getMessage());
            return false;
        }
    }

    public int insertInArtist(String name) {

        try {

            insertArtist.setString(1, name);
            int affectedRows = insertArtist.executeUpdate();

            if (affectedRows != 1) {
                throw new SQLException("Couldn't insert in artist");
            }

            ResultSet generatedKeys = insertArtist.getGeneratedKeys();
            if (generatedKeys.next()) {
                return generatedKeys.getInt(1);
            } else
                throw new SQLException("Couldn't get _id for artist");


        } catch (SQLException e) {
            System.out.println();
            return -1;
        }


    }

    public int insertInAlbum(int id, String name) {

        try {


            insertAlbum.setString(1, name);
            insertAlbum.setInt(2, id);
            int affectedRows = insertAlbum.executeUpdate();


            if (affectedRows != 1) {
                throw new SQLException("Couldn't insert in artist");
            }

            ResultSet generatedKeys = insertAlbum.getGeneratedKeys();
            if (generatedKeys.next()) {
                return generatedKeys.getInt(1);
            } else
                throw new SQLException("Couldn't get _id for artist");


        } catch (SQLException e) {
            System.out.println();
            return -1;
        }


    }

    public int insertInSong(int id, String name) {

        try {

            int track = queryMaxTrack(id);

            insertSong.setInt(1, track + 1);
            insertSong.setString(2, name);
            insertSong.setInt(3, id);
            int affectedRows = insertSong.executeUpdate();


            if (affectedRows != 1) {
                throw new SQLException("Couldn't insert in artist");
            }

            ResultSet generatedKeys = insertSong.getGeneratedKeys();
            if (generatedKeys.next()) {
                return generatedKeys.getInt(1);
            } else
                throw new SQLException("Couldn't get _id for artist");

        } catch (SQLException e) {
            System.out.println();
            return -1;
        }


    }


    public boolean deleteArtist(int id) {

        try {

            conn.setAutoCommit(false);

            deleteSongs(id);
            deleteAlbums(id);


            deleteArtist.setInt(1, id);


            int affectedRows = deleteArtist.executeUpdate();

            if (affectedRows == 1) {
                conn.commit();
                return true;

            } else {
                throw new SQLException("Couldn't insert into songs");

            }

        } catch (Exception e) {
            System.out.println("Insert song exception " + e.getMessage());
            try {
                System.out.println("Performing rollback");
                conn.rollback();

            } catch (SQLException e2) {
                System.out.println("RollBack exception " + e2.getMessage());
            }
        } finally {
            try {
                System.out.println("Restoring default commit behavior");
                conn.setAutoCommit(true);

            } catch (SQLException e) {
                System.out.println("Reset auto-commit" + e.getMessage());
            }

        }
        return false;

    }

    private void deleteAlbums(int artistId) {

        try {

            deleteAlbums.setInt(1, artistId);
            deleteAlbums.executeUpdate();


        } catch (SQLException e) {
            System.out.println("Err delete album " + e.getMessage());
            e.printStackTrace();
        }


    }

    private void deleteSongs(int id) {

        try {

            deleteSongs.setInt(1, id);
            deleteSongs.executeUpdate();


        } catch (SQLException e) {
            System.out.println("Err delete album " + e.getMessage());
            e.printStackTrace();
        }
    }

    public boolean deleteAlbumById(int id) {
        try {

            conn.setAutoCommit(false);

            deleteSongsByAlbumId(id);


            deleteAlbumByID.setInt(1, id);


            int affectedRows = deleteAlbumByID.executeUpdate();

            if (affectedRows == 1) {
                conn.commit();
                return true;

            } else {
                throw new SQLException("Couldn't insert into songs");

            }

        } catch (Exception e) {
            System.out.println("Insert song exception " + e.getMessage());
            try {
                System.out.println("Performing rollback");
                conn.rollback();

            } catch (SQLException e2) {
                System.out.println("RollBack exception " + e2.getMessage());
            }
        } finally {
            try {
                System.out.println("Restoring default commit behavior");
                conn.setAutoCommit(true);

            } catch (SQLException e) {
                System.out.println("Reset auto-commit" + e.getMessage());
            }

        }
        return false;


    }

    private void deleteSongsByAlbumId(int id) {
        try {

            deleteSongByAlbumID.setInt(1, id);
            deleteSongByAlbumID.executeUpdate();

        } catch (SQLException e) {
            System.out.println("Could delete from songs " + e.getMessage());
            e.printStackTrace();
        }
    }

    public boolean deleteSongBySongId(int id) {
        try {

            deleteSongBySongID.setInt(1, id);
            int affectedRows = deleteSongBySongID.executeUpdate();
            return affectedRows == 1;

        } catch (SQLException e) {
            System.out.println("Couldn't delete song " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

}
