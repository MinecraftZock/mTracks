package info.hannes.mechadmin_gen.ops;

import android.os.Environment;
import android.util.Log;

import com.robotoworks.mechanoid.db.SQuery;
import com.robotoworks.mechanoid.db.SQuery.Op;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.List;

import info.hannes.mechadmin.LoggingHelperAdmin;
import info.hannes.mechadmin_gen.sqlite.MxAdminDBContract.PictureStage;
import info.hannes.mechadmin_gen.sqlite.MxAdminDBContract.Videos;
import info.hannes.mechadmin_gen.sqlite.PictureStageRecord;
import info.hannes.mechadmin_gen.sqlite.TrackstageBrotherRecord;
import info.hannes.mechadmin_gen.sqlite.VideosRecord;
import timber.log.Timber;

public class ImportDataBrother {

    public static String download2xHtml(TrackstageBrotherRecord track, String url, String destFilename, int zlr) throws IOException {
        Document doc;
        String filename = "";
        String www = null;
        String notes = null;
        LoggingHelperAdmin.setMessage(zlr + " Download " + url);
        List<TextNode> zeiten = null;
        if (track.getContentDetailXml().equals("")) {
            filename = Environment.getExternalStorageDirectory() + "/" + destFilename;
            LoggingHelperAdmin.setMessage("Jsoup " + destFilename);
            // final Document doc = Jsoup.parse(url, null);
            doc = Jsoup.connect(url)
                    .timeout(60 * 1000)
                    .userAgent("Mozilla/5.0 (Windows NT 5.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/27.0.1453.110 Safari/537.36")
                    // .execute().parse();
                    .get();
            // final String output = doc.toString();
            // writeString2File(output, filename, context);
            track.setContentDetailXml(doc.toString());
            track.save(false);
        } else {
            doc = Jsoup.parse(track.getContentDetailXml());
        }
        // final Connection connectionTest = Jsoup.connect(url).timeout(60 * 1000); // prevents timeout
        // final Document doc = Jsoup.parse(new String(connectionTest.execute().bodyAsBytes(), "UTF-8")); // sonderzeichen
        // output = escapeHtml(doc.toString());
        final String urlPost = url.substring(url.lastIndexOf("/") + 1);
        final String urlPre = url.substring(0, url.lastIndexOf("/"));

        final Elements zeitenContainer = doc.select("div[class=views-field views-field-title]");
        for (final org.jsoup.nodes.Element row : zeitenContainer.select("div[id=inner_right]")) {
            zeiten = row.textNodes();
            notes = row.nextElementSibling().nextSibling().toString().replace(".", "").trim();
        }

        SQuery.newQuery().expr(PictureStage.REST_ID, Op.EQ, 0).delete(PictureStage.CONTENT_URI);
        SQuery.newQuery().expr(Videos.REST_ID, Op.EQ, 0).delete(Videos.CONTENT_URI);

        final Elements trackVids = doc.select("div[class=views-field views-field-field-track-video-s]")
                .select("div[class=field-content]").select("div[align=center]");
        for (final Element elemVid : trackVids) {
            final Elements vid = elemVid.select("a");
            final String vidUrl = urlPre + vid.first().attr("href");
            VideosRecord videoRec = SQuery.newQuery()
                    .expr(Videos.TRACK_ID, Op.EQ, track.getId())
                    .expr(Videos.WWW, Op.EQ, vidUrl)
                    .selectFirst(Videos.CONTENT_URI);
            if (videoRec == null) {
                videoRec = new VideosRecord();
                videoRec.setChanged(System.currentTimeMillis());
                videoRec.setTrackId(track.getId());
                videoRec.setTrackRestId(track.getRestId());
                videoRec.setWww(vidUrl);
                videoRec.save(false);
            } else {
                videoRec.setTrackRestId(track.getRestId());
                videoRec.save(false);
            }
        }

        final Elements trackImg = doc.select("div[class=gallery-frame]").select("ul").select("li").select("img");
        for (final Element elem : trackImg) {
            for (final Attribute attr : elem.attributes()) {
                if (attr.getKey().equals("src")) {
                    String img = attr.getValue();
                    img = img.substring(0, img.indexOf("?"));
                    PictureStageRecord picRec = SQuery.newQuery()
                            .expr(PictureStage.TRACK_ID, Op.EQ, track.getId())
                            .expr(PictureStage.WWW, Op.EQ, img)
                            .selectFirst(PictureStage.CONTENT_URI);
                    if (picRec == null) {
                        picRec = new PictureStageRecord();
                        picRec.setChanged(System.currentTimeMillis());
                        picRec.setTrackId(track.getId());
                        picRec.setTrackRestId(track.getRestId());
                        picRec.setWww(img);
                        picRec.save(false);
                    } else {
                        picRec.setTrackRestId(track.getRestId());
                        picRec.save(false);
                    }
                }
            }
        }
        String media = "";
        if (trackImg != null && trackImg.size() == 0) {
            media = "Bilder:" + trackImg.size() + " ";
        }
        if (trackVids != null && trackVids.size() == 0) {
            media = media + "Videos:" + trackVids.size();
        }
        if (!media.equals("")) {
            Log.i(urlPost, media);
        }

        final Elements trackContainer = doc.select("div[class=clearfix fivestar-average-stars fivestar-average-text]");
        final String votes = trackContainer.text();
        final List<TextNode> trackNodes = trackContainer.last().parent().textNodes();
        final Elements trackLinks = trackContainer.last().parent().getElementsByAttribute("href");
        String telephone = "";
        for (final TextNode node : trackNodes) {
            if (node.text().contains("Telephone:")) {
                telephone += node.text().substring(node.text().indexOf(":") + 1).trim() + "\n";
            }
        }
        telephone = telephone.replaceAll(" ", "").trim();

        for (final Element node : trackLinks) {
            for (final Attribute attr : node.attributes()) {
                if (attr.getKey().equals("href") && attr.getValue().startsWith("http")) {
                    www = attr.getValue().toLowerCase();
                    if (www.endsWith("/")) {
                        www = www.substring(0, www.length() - 1);
                    }
                }
            }
        }
        if (telephone != null && !telephone.equals("")) {
            Log.i(urlPost, "telephone:" + telephone);
            track.setPhone(telephone);
        }
        if (www != null && !www.equals("")) {
            Log.i(urlPost, "www:" + www);
            track.setUrl(www);
        }
        if (notes != null && notes.equals("Members only")) {
            notes = "";
            track.setTrackaccess("M");
        } else if (notes != null && notes.toLowerCase().contains("race")) {
            track.setTrackaccess("R");
        } else {
            track.setTrackaccess("N");
        }
        if (notes != null && !notes.equals("")) {
            notes = notes.replaceAll("00", "")
                    .replaceAll("30", ":30")
                    .replaceAll("::", ":")
                    .replace("betwee ", "between")
                    .replaceAll(" :30", " 30")
                    .replaceAll("&nbsp;", " ")
                    .replaceAll("&amp;", "&")
                    .replaceAll("&gt;", ">")
                    .replaceAll("&lt;", "<");
            Timber.i(urlPost, "note:" + notes);
            track.setNotes(notes);
        }
        if (votes != null && !votes.contains("No votes yet")) {
            Timber.i(urlPost, "votes:" + votes);
        }
        if (zeiten != null) {
            if (zeiten.size() != 8) {
                Timber.w(urlPost, "zeitenerror:" + zeiten.size());
            } else {
                final boolean allClosed = zeiten.toString().replace("[", "").replace("]", "")
                        .replaceAll("closed", "").replaceAll(",", "").trim().equals("");
                final String zeitGes = zeiten.toString().replace("[", "").replace("]", "")
                        .replaceAll(":00", "")
                        .replaceAll("closed", "")
                        .replaceAll("  ", "")
                        .replaceAll(",", "")
                        .trim();
                if (!zeitGes.equals("")) {
                    Timber.i(urlPost, "zeiten:" + zeitGes);
                }
                String zeit;

                zeit = zeiten.get(0).toString().replaceAll(" ", "").trim();
                track.setHoursmonday(zeit.replaceAll("closed", "").replaceAll(":00", "").replaceAll(" ", ""));
                if (allClosed) {
                    track.setOpenmondays(0);
                } else if (zeit.equals("closed")) {
                    track.setOpenmondays(-1);
                } else {
                    track.setOpenmondays(1);
                }

                zeit = zeiten.get(1).toString().replaceAll(" ", "");
                track.setHourstuesday(zeit.replaceAll("closed", "").replaceAll(":00", "").replaceAll(" ", ""));
                if (allClosed) {
                    track.setOpentuesdays(0);
                } else if (zeit.equals("closed")) {
                    track.setOpentuesdays(-1);
                } else {
                    track.setOpentuesdays(1);
                }

                zeit = zeiten.get(2).toString().replaceAll(" ", "");
                track.setHourswednesday(zeit.replaceAll("closed", "").replaceAll(":00", "").replaceAll(" ", ""));
                if (allClosed) {
                    track.setOpenwednesday(0);
                } else if (zeit.equals("closed")) {
                    track.setOpenwednesday(-1);
                } else {
                    track.setOpenwednesday(1);
                }

                zeit = zeiten.get(3).toString().replaceAll(" ", "");
                track.setHoursthursday(zeit.replaceAll("closed", "").replaceAll(":00", "").replaceAll(" ", ""));
                if (allClosed) {
                    track.setOpenthursday(0);
                } else if (zeit.equals("closed")) {
                    track.setOpenthursday(-1);
                } else {
                    track.setOpenthursday(1);
                }

                zeit = zeiten.get(4).toString().replaceAll(" ", "");
                track.setHoursfriday(zeit.replaceAll("closed", "").replaceAll(":00", "").replaceAll(" ", ""));
                if (allClosed) {
                    track.setOpenfriday(0);
                } else if (zeit.equals("closed")) {
                    track.setOpenfriday(-1);
                } else {
                    track.setOpenfriday(1);
                }

                zeit = zeiten.get(5).toString().replaceAll(" ", "");
                track.setHourssaturday(zeit.replaceAll("closed", "").replaceAll(":00", "").replaceAll(" ", ""));
                if (allClosed) {
                    track.setOpensaturday(0);
                } else if (zeit.equals("closed")) {
                    track.setOpensaturday(-1);
                } else {
                    track.setOpensaturday(1);
                }

                zeit = zeiten.get(6).toString().replaceAll(" ", "");
                track.setHourssunday(zeit.replaceAll("closed", "").replaceAll(":00", "").replaceAll(" ", ""));
                if (allClosed) {
                    track.setOpensunday(0);
                } else if (zeit.equals("closed")) {
                    track.setOpensunday(-1);
                } else {
                    track.setOpensunday(1);
                }

            }
        } else {
            Timber.w(urlPost, "zeiten_null");
        }
        track.save(false);
        return filename;
    }
}
