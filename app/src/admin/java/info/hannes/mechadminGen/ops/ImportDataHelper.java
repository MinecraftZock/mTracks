package info.hannes.mechadminGen.ops;

import android.content.ContentValues;
import android.content.Context;
import android.os.Environment;

import com.robotoworks.mechanoid.db.SQuery;
import com.robotoworks.mechanoid.db.SQuery.Op;
import com.robotoworks.mechanoid.net.Response;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import info.hannes.mechadmin.LoggingHelperAdmin;
import info.hannes.mechadminGen.rest.MxCal;
import info.hannes.mechadminGen.rest.PostMXcalSerieRequest;
import info.hannes.mechadminGen.rest.PostMXcalSerieResult;
import info.hannes.mechadminGen.rest.PostMXcalSeriestrackRequest;
import info.hannes.mechadminGen.rest.PostMXcalSeriestrackResult;
import info.hannes.mechadminGen.rest.PostMXcalTrackRequest;
import info.hannes.mechadminGen.rest.PostMXcalTrackResult;
import info.hannes.mechadminGen.rest.PutMXcalSerieRequest;
import info.hannes.mechadminGen.rest.PutMXcalSerieResult;
import info.hannes.mechadminGen.rest.RESTmxcalSerie;
import info.hannes.mechadminGen.rest.RESTmxcalSeriestrack;
import info.hannes.mechadminGen.rest.RESTmxcalTrack;
import info.hannes.mechadminGen.rest.Serie;
import info.hannes.mechadminGen.rest.SeriesTracks;
import info.hannes.mechadminGen.sqlite.MXSerieRecord;
import info.hannes.mechadminGen.sqlite.MxCalContract.MXSerie;
import info.hannes.mechadminGen.sqlite.MxCalContract.MxSeriesTrack;
import info.hannes.mechadminGen.sqlite.MxCalContract.MxTrack;
import info.hannes.mechadminGen.sqlite.MxSeriesTrackRecord;
import info.hannes.mechadminGen.sqlite.MxTrackRecord;
import info.hannes.mechadminGen.sqlite.QuellFileRecord;
import info.hannes.retrofit.ApiAdminClient;
import info.mx.comlib.retrofit.service.model.InsertResponse;
import okhttp3.MultipartBody;
import retrofit2.Call;
import timber.log.Timber;

@SuppressWarnings("ALL")
public class ImportDataHelper {

    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault());
    private static SimpleDateFormat sdfTag = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

    public static String download2xHtml(String url, String destFilename) throws IOException {
        LoggingHelperAdmin.setMessage("Download " + url);

        final String filename = Environment.getExternalStorageDirectory() + "/" + destFilename;
        LoggingHelperAdmin.setMessage("Jsoup " + destFilename);
        // final Document doc = Jsoup.parse(url, null);
        // final Document doc = Jsoup.connect(url).timeout(60 * 1000).get();
        final Connection connectionTest = Jsoup.connect(url).timeout(60 * 1000); // prevents timeout
        final Document doc = Jsoup.parse(new String(connectionTest.execute().bodyAsBytes(), StandardCharsets.UTF_8)); // sonderzeichen
        final String output = doc.toString();

        writeString2File(output, filename);

        return filename;
    }

    private static void writeString2File(String output, String filename) {
        try {
            final File file = new File(filename);
            final FileOutputStream fos = new FileOutputStream(file);
            fos.write(output.getBytes());
            fos.close();
        } catch (final IOException e) {
            Timber.e(e);
        }
    }

    public static String doTransform2UrlList(Context context, String filename, String xsltFile, String resFile) {
        String res = "";
        try {
            final Source xmlSource = new StreamSource(new BufferedInputStream(new FileInputStream(filename)));
            final Source xsltSource = new StreamSource(context.getAssets().open(xsltFile));

            final TransformerFactory transFact = TransformerFactory.newInstance();
            final Transformer trans = transFact.newTransformer(xsltSource);
            res = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + resFile;
            final File f = new File(res);
            final StreamResult result = new StreamResult(f);
            trans.transform(xmlSource, result);

        } catch (final TransformerFactoryConfigurationError | IOException | TransformerException e) {
            Timber.e(e);
        }
        return res;
    }

    public static void doRemoveContent(String oldFileName, String indicator) {
        final String tmpFileName = oldFileName + ".new";

        try (BufferedReader br = new BufferedReader(new FileReader(oldFileName)); BufferedWriter bw = new BufferedWriter(new FileWriter(tmpFileName))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (!(line.contains("<") || line.contains(indicator) || line.contains("<?xml") || line.contains("top>"))) {
                    // skip
                } else {
                    line = line.trim();
                    bw.write(line + "\n");
                }
            }
        } catch (final Exception e) {
            return;
        }

        // Once everything is complete, delete old file..
        final File oldFile = new File(oldFileName);
        oldFile.delete();

        // And rename tmp file's name to old file name
        final File newFile = new File(tmpFileName);
        newFile.renameTo(oldFile);
    }

    public static void doCleanXml(String oldFileName) {
        final String tmpFileName = oldFileName + ".new";

        BufferedReader br = null;
        BufferedWriter bw = null;
        try {
            br = new BufferedReader(new FileReader(oldFileName));
            bw = new BufferedWriter(new FileWriter(tmpFileName));
            String line;
            while ((line = br.readLine()) != null) {
                if (line.contains("<!DOCTYPE html") || line.contains("xhtml1-transitional.dtd")) {
                    // auslassen
                } else {
                    if (line.contains("<html xmlns")) {
                        line = "<html>";
                    } else {
                        line = line
                                .replace("&nbsp;", "")
                                .replace("&acirc;", "")
                                .replace("&iuml;", "")
                                .replace("&acirc;", "")
                                .replace("&raquo;", "")
                                .replace("&iquest;", "")
                                .replace("&copy;", "");
                    }
                    bw.write(line + "\n");
                }
            }
        } catch (final Exception e) {
            return;
        } finally {
            try {
                if (br != null) {
                    br.close();
                }
            } catch (final IOException e) {
                //
            }
            try {
                if (bw != null) {
                    bw.close();
                }
            } catch (final IOException e) {
                //
            }
        }
        // Once everything is complete, delete old file..
        final File oldFile = new File(oldFileName);
        oldFile.delete();

        // And rename tmp file's name to old file name
        final File newFile = new File(tmpFileName);
        newFile.renameTo(oldFile);
    }

    public static ImportMxCalResult importMXSerieFromHttp(ImportMxCalResult res, Serie serieWeb, QuellFileRecord quellFile) throws ParseException {
        MXSerieRecord serieDB;

        long id = SQuery.newQuery()
                .expr(MXSerie.WEB_ID, Op.EQ, serieWeb.getId())
                .firstLong(MXSerie.CONTENT_URI, MXSerie._ID);
        if (id == 0) {
            serieDB = new MXSerieRecord();
            serieDB.setWebId(serieWeb.getId());
        } else {
            serieDB = MXSerieRecord.get(id);
        }
        long updatedWeb = sdf.parse(serieWeb.getUpdatedAt()).getTime() / 1000;
        res.serieGes++;
        if (serieDB.getUpdatedAt() < updatedWeb) {
            res.serie++;
            serieDB.setCreatedAt(sdf.parse(serieWeb.getCreatedAt()).getTime() / 1000);
            serieDB.setUpdatedAt(updatedWeb);
            serieDB.setName(serieWeb.getName());
            serieDB.setSeriesUrl(serieWeb.getSeriesUrl());
            serieDB.setYear(serieWeb.getYear());
            serieDB.setQuellfileId(quellFile.getId() * -1);
            serieDB.save();
        }
        for (final SeriesTracks trackWeb : serieWeb.getSeriesTracks()) {
            MxSeriesTrackRecord seriesTrackDB;
            id = SQuery.newQuery()
                    .expr(MxSeriesTrack.WEB_ID, Op.EQ, trackWeb.getId())
                    .firstLong(MxSeriesTrack.CONTENT_URI, MxSeriesTrack._ID);
            if (id == 0) {
                seriesTrackDB = new MxSeriesTrackRecord();
                seriesTrackDB.setWebId(trackWeb.getId());
            } else {
                seriesTrackDB = MxSeriesTrackRecord.get(id);
            }
            updatedWeb = sdf.parse(trackWeb.getUpdatedAt()).getTime() / 1000;
            res.seriesTrackGes++;
            if (seriesTrackDB.getUpdatedAt() < updatedWeb) {
                res.seriesTrack++;
                seriesTrackDB.setCreatedAt(sdf.parse(trackWeb.getCreatedAt()).getTime() / 1000);
                seriesTrackDB.setUpdatedAt(updatedWeb);
                seriesTrackDB.setEventDate(sdfTag.parse(trackWeb.getEventDate()).getTime() / 1000);
                seriesTrackDB.setNotes(trackWeb.getNotes());
                seriesTrackDB.setQuellfileId(quellFile.getId() * -1);
                seriesTrackDB.setWebSeriesId(trackWeb.getSeriesId());
                seriesTrackDB.setWebTrackId(trackWeb.getTrackId());
                seriesTrackDB.save();
            }
            if (trackWeb.getTrack() != null) {
                MxTrackRecord trackDB;
                id = SQuery.newQuery()
                        .expr(MxTrack.WEB_ID, Op.EQ, trackWeb.getTrack().getId())
                        .firstLong(MxTrack.CONTENT_URI, MxTrack._ID);
                if (id == 0) {
                    trackDB = new MxTrackRecord();
                    trackDB.setWebId(trackWeb.getTrack().getId());
                } else {
                    trackDB = MxTrackRecord.get(id);
                }
                updatedWeb = sdf.parse(trackWeb.getTrack().getUpdatedAt()).getTime() / 1000;
                res.trackGes++;
                if (trackDB.getUpdatedAt() < updatedWeb) {
                    res.track++;
                    trackDB.setCreatedAt(sdf.parse(trackWeb.getTrack().getCreatedAt()).getTime() / 1000);
                    trackDB.setUpdatedAt(updatedWeb);
                    trackDB.setQuellfileId(quellFile.getId() * -1);
                    trackDB.setAddress(trackWeb.getTrack().getAddress());
                    trackDB.setCity(trackWeb.getTrack().getCity());
                    trackDB.setEmail(trackWeb.getTrack().getEmail());
                    trackDB.setName(trackWeb.getTrack().getName());
                    trackDB.setPhone(trackWeb.getTrack().getPhone());
                    trackDB.setStateCode(trackWeb.getTrack().getStateCode());
                    trackDB.setWebsite(trackWeb.getTrack().getWebsite());
                    trackDB.setZip(trackWeb.getTrack().getZip());
                    trackDB.save();
                }
            }
        }
        quellFile.setUpdatedCount(res.track + res.seriesTrack + res.track);
        quellFile.setLog("Serie:" + res.serie + "/" + res.serieGes + "\n" +
                "SeriesTrack:" + res.seriesTrack + "/" + res.seriesTrackGes + "\n" +
                "Track:" + res.track + "/" + res.trackGes);
        quellFile.save();
        LoggingHelperAdmin.setMessage("");
        return res;
    }

    public static class ImportMxCalResult {
        public int track;
        public int trackGes;
        public int serie;
        public int serieGes;
        public int seriesTrack;
        public int seriesTrackGes;

        public ImportMxCalResult() {
            track = 0;
            trackGes = 0;
            serie = 0;
            serieGes = 0;
            seriesTrack = 0;
            seriesTrackGes = 0;
        }
    }

    public static void SendMXSerie2Server(MxCal webClient, MXSerieRecord serie) throws Exception {
        LoggingHelperAdmin.setMessage("POST Serie" + serie.getId());
        if (serie.getRestId() == 0) {
            final RESTmxcalSerie serieReq = new RESTmxcalSerie();
            serieReq.setName(serie.getName());
            serieReq.setChanged((int) serie.getUpdatedAt());
            serieReq.setQuellfileId((int) serie.getQuellfileId());
            serieReq.setSeriesUrl(serie.getSeriesUrl());// (URLEncoder.encode(serie.getSeriesUrl(), "UTF-8"));
            serieReq.setWebid((int) serie.getWebId());
            serieReq.setYear(serie.getYear());
            final PostMXcalSerieRequest request = new PostMXcalSerieRequest(serieReq);
            final Response<PostMXcalSerieResult> res = webClient.postMXcalSerie(request);
            final PostMXcalSerieResult resParse = res.parse();
            res.checkResponseCodeOk();
            serie.setRestId(resParse.getCalBaseResponse().getId());
            serie.save();
        } else {
            final RESTmxcalSerie serieReq = new RESTmxcalSerie();
            serieReq.setName(serie.getName());
            serieReq.setChanged((int) serie.getUpdatedAt());
            serieReq.setQuellfileId((int) serie.getQuellfileId());
            serieReq.setSeriesUrl(serie.getSeriesUrl());// (URLEncoder.encode(serie.getSeriesUrl(), "UTF-8"));
            serieReq.setWebid((int) serie.getWebId());
            serieReq.setYear(serie.getYear());
            final PutMXcalSerieRequest request = new PutMXcalSerieRequest(serie.getId(), serieReq);
            final Response<PutMXcalSerieResult> res = webClient.putMXcalSerie(request);
            res.parse();
            res.checkResponseCode(204);
            // serie.setRestId(resParse.getBaseResponse().getId());
            // serie.save();
            // throw new Exception("PUT Serie implementieren !");
        }
    }

    public static void SendMXTrack2Server(MxCal webClient, MxTrackRecord track) throws Exception {
        LoggingHelperAdmin.setMessage("POST Track" + track.getId());
        if (track.getRestId() == 0) {
            final RESTmxcalTrack serieReq = new RESTmxcalTrack();
            serieReq.setName(track.getName());
            serieReq.setChanged((int) track.getUpdatedAt());
            serieReq.setQuellfileId((int) track.getQuellfileId());
            serieReq.setAddress(track.getAddress());
            serieReq.setCity(track.getCity());
            serieReq.setEmail(track.getEmail());
            serieReq.setPhone(track.getPhone());
            serieReq.setStateCode("US");
            // serieReq.setStateCode(track.getStateCode());
            serieReq.setWebid((int) track.getWebId());
            serieReq.setWebsite(track.getWebsite());
            serieReq.setZip((int) track.getZip());
            final PostMXcalTrackRequest request = new PostMXcalTrackRequest(serieReq);
            final Response<PostMXcalTrackResult> res = webClient.postMXcalTrack(request);
            final PostMXcalTrackResult resParse = res.parse();
            res.checkResponseCodeOk();
            track.setRestId(resParse.getCalBaseResponse().getId());
            track.save();
        } else {
            throw new Exception("PUT Tracks implementieren !");
        }
    }

    public static void SendMXSeriesTrack2Server(MxCal webClient, MxSeriesTrackRecord seriesTrack) throws Exception {
        long trackWebID = seriesTrack.getWebTrackId();
        long serieWebID = seriesTrack.getWebSeriesId();
        LoggingHelperAdmin.setMessage("POST SeriesTrack" + seriesTrack.getId() + " " + trackWebID);
        final MXSerieRecord serie = SQuery.newQuery()
                .expr(MXSerie.WEB_ID, Op.EQ, seriesTrack.getWebSeriesId())
                .selectFirst(MXSerie.CONTENT_URI, MXSerie._ID);
        final long alternativSerieWebId = SQuery.newQuery()
                .expr(MXSerie.NAME, Op.LIKE, "%" + serie.getName().trim() + "%")
                .firstLong(MXSerie.CONTENT_URI, MXSerie.WEB_ID, MXSerie.CREATED_AT + " asc," + MXSerie.WEB_ID + " asc");
        if (alternativSerieWebId != seriesTrack.getWebSeriesId()) {
            Timber.w("Serie:" + serieWebID + " alter:" + alternativSerieWebId);
            serieWebID = alternativSerieWebId;
        }

        final MxTrackRecord track = SQuery.newQuery()
                .expr(MxTrack.WEB_ID, Op.EQ, seriesTrack.getWebTrackId())
                .selectFirst(MxTrack.CONTENT_URI, MxTrack._ID);
        final long alternativTrackWebId = SQuery.newQuery()
                .expr(MxTrack.NAME, Op.LIKE, "%" + track.getName().trim() + "%")
                .firstLong(MxTrack.CONTENT_URI, MxTrack.WEB_ID, MxTrack.CREATED_AT + " asc," + MxTrack.WEB_ID + " asc");
        if (alternativTrackWebId != seriesTrack.getWebTrackId()) {
            Timber.w("Track:" + trackWebID + " alter:" + alternativTrackWebId);
            trackWebID = alternativTrackWebId;
        }
        if (seriesTrack.getRestId() == 0) {
            final RESTmxcalSeriestrack serieReq = new RESTmxcalSeriestrack();
            serieReq.setChanged((int) seriesTrack.getUpdatedAt());
            serieReq.setQuellfileId((int) seriesTrack.getQuellfileId());
            serieReq.setWebid((int) seriesTrack.getWebId());
            serieReq.setEventDate((int) seriesTrack.getEventDate());
            serieReq.setNotes(seriesTrack.getNotes());
            serieReq.setWebSeriesId((int) serieWebID);
            serieReq.setWebTrackId((int) trackWebID);
            final PostMXcalSeriestrackRequest request = new PostMXcalSeriestrackRequest(serieReq);
            final Response<PostMXcalSeriestrackResult> res = webClient.postMXcalSeriestrack(request);
            final PostMXcalSeriestrackResult resParse = res.parse();
            res.checkResponseCodeOk();
            seriesTrack.setRestId(resParse.getCalInsertResponse().getId());
            if (!resParse.getCalInsertResponse().getMessage().equals("")) {
                throw new Exception("POST SeriesTrack " + resParse.getCalInsertResponse().getMessage());
            }
            seriesTrack.save();
        } else {
            throw new Exception("PUT SeriesTrack implement !");
        }
    }

    public static void SendDownLoadSite2ServerMultipart(ApiAdminClient apiAdminClient, QuellFileRecord download, String basic) {
        final long start = System.currentTimeMillis();
        LoggingHelperAdmin.setMessage("POST DownloadSite " + download.getId());
        if (download.getRestId() == 0) {

            MultipartBody.Part filePart = MultipartBody.Part.createFormData("content", download.getContent());

            Call<InsertResponse> call = apiAdminClient.getAdminService().postMXCalFile(
                    download.getLog(),
                    download.getCreatedate(),
                    download.getUrl(),
                    download.getUpdatedCount(),
                    filePart, basic);
            try {
                retrofit2.Response<InsertResponse> postRes = call.execute();
                if (postRes.body().getId() > 0) {
                    download.setRestId(postRes.body().getId());
                    download.save();

                    final ContentValues values = new ContentValues();
                    values.put(MxTrack.QUELLFILE_ID, download.getRestId());
                    final int anzT = SQuery.newQuery().expr(MxTrack.QUELLFILE_ID, Op.EQ, download.getId() * -1).update(MxTrack.CONTENT_URI, values);
                    final int anzST = SQuery.newQuery().expr(MxSeriesTrack.QUELLFILE_ID, Op.EQ, download.getId() * -1)
                            .update(MxSeriesTrack.CONTENT_URI, values);
                    final int anzS = SQuery.newQuery().expr(MXSerie.QUELLFILE_ID, Op.EQ, download.getId() * -1).update(MXSerie.CONTENT_URI, values);
                    Timber.d(anzT + " " + anzST + " " + anzS);
                }
                Timber.d("doPostImage " + (System.currentTimeMillis() - start) + "ms");
            } catch (IOException e) {
                Timber.e(e);
            }
        }
    }

    public static String getFileToken(String url) {
        String filetoken = url;
        filetoken = filetoken.substring(filetoken.lastIndexOf("/") + 1, filetoken.lastIndexOf("."));
        final File mFolder = new File(Environment.getExternalStorageDirectory() + "/mxinfo");
        if (!mFolder.exists()) {
            mFolder.mkdir();
        }
        return Environment.getExternalStorageDirectory() + "/mxinfo/" + filetoken + ".xml";
    }
}
