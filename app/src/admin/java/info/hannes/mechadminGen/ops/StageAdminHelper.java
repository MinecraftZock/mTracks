package info.hannes.mechadminGen.ops;

import com.robotoworks.mechanoid.db.SQuery;
import com.robotoworks.mechanoid.db.SQuery.Op;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.List;

import timber.log.Timber;
import info.mx.core.common.LoggingHelper;
import info.mx.tracks.common.SecHelper;
import info.mx.core_generated.sqlite.MxInfoDBContract.Tracks;
import info.mx.core_generated.sqlite.MxInfoDBContract.Trackstage;
import info.mx.core_generated.sqlite.TracksRecord;
import info.mx.core_generated.sqlite.TrackstageRecord;

public class StageAdminHelper {

    public static void doStagesSplitToAcceptableAskable() {
        final List<TrackstageRecord> records = SQuery.newQuery()
                .expr(Trackstage.APPROVED, Op.EQ, 0)
                .expr(Trackstage.TRACK_REST_ID, Op.GT, 0)
                .select(Trackstage.CONTENT_URI, Trackstage._ID);
        int i = 0;
        for (final TrackstageRecord record : records) {
            i++;
            LoggingHelper.INSTANCE.setMessage("split:" + i + "/" + records.size());
            StageAdminHelper.checkProblematicChanges(record.getTrackRestId(), record);
        }
        LoggingHelper.INSTANCE.setMessage("");
    }

    public static String checkProblematicChanges(long trackRestID, TrackstageRecord recentStage) {
        String res = "";
        boolean cleared = false;
        final TrackstageRecord newStage = new TrackstageRecord(); // optional, without changes it's discharged
        newStage.setTrackRestId(recentStage.getTrackRestId());

        final TracksRecord track = SQuery.newQuery().expr(Tracks.REST_ID, Op.EQ, trackRestID).selectFirst(Tracks.CONTENT_URI);
        if (track == null) {
            Timber.e("track nicht gefunden:" + trackRestID);
        }
        final Method[] methodsStage = TrackstageRecord.class.getDeclaredMethods();
        final Method[] methodsTrack = TracksRecord.class.getDeclaredMethods();
        for (int i = 0; i < methodsStage.length && track != null; i++) {
            try {

                if (methodsStage[i].getName().startsWith("get") &&
                        !methodsStage[i].getName().equals("getFactory") &&
                        !methodsStage[i].getName().equals("get") &&
                        !methodsStage[i].getName().equals("getLatitude") &&
                        !methodsStage[i].getName().equals("getLongitude") &&
                        !methodsStage[i].getName().startsWith("getId") &&
                        !methodsStage[i].getName().startsWith("getApproved") &&
                        !methodsStage[i].getName().startsWith("getRestId") &&
                        !methodsStage[i].getName().startsWith("getAndroidid")) {
                    final Method trackMethod = getMethode(methodsTrack, methodsStage[i].getName());
                    if (trackMethod != null) {
                        final Object fobjStage = methodsStage[i].invoke(recentStage, (Object[]) null);
                        final Object fobjTrack = trackMethod.invoke(track, (Object[]) null);
                        if (fobjStage != null && fobjTrack != null && fobjStage.toString() != null && fobjTrack.toString() != null) {
                            final String valueStage = fobjStage.toString().trim();
                            String valueTrack = fobjTrack.toString().trim();
                            // crypt
                            if (methodsStage[i].getName().startsWith("getPhone") ||
                                    methodsStage[i].getName().startsWith("getContact") ||
                                    methodsStage[i].getName().startsWith("getAdress") ||
                                    methodsStage[i].getName().startsWith("getUrl")) {
                                valueTrack = SecHelper.decryptB64(valueTrack);
                            }
                            if (valueStage.equals(valueTrack) || valueStage.equals("0") || valueStage.equals("0.0") || valueStage.equals("")) {
                                if (valueStage.equals("0") || valueStage.equals("0.0") || valueStage.equals("")) {
                                    Timber.d(recentStage.getRestId() + "/" + recentStage.getTrackRestId() + " leer:" + methodsStage[i].getName() +
                                            "() stage/track:" + valueStage + "/" + valueTrack);
                                } else {
                                    Timber.d(recentStage.getRestId() + "/" + recentStage.getTrackRestId() + " gleich:" + methodsStage[i].getName() +
                                            "() stage/track:" + valueStage + "/" + valueTrack);
                                }
                            } else {
                                Timber.i(recentStage.getRestId() + "/" + recentStage.getTrackRestId() + " verschieden stage/track:"
                                        + methodsStage[i].getName() + "() stage/track:" + valueStage + "/" + valueTrack);
                                final Method stageSetMethod = getMethode(methodsStage, "set" + methodsStage[i].getName().substring(3));
                                cleared = true;
                                final Type listType = stageSetMethod.getGenericParameterTypes()[0];

                                if (listType.toString().equals("long") || listType.toString().equals("int")) {
                                    stageSetMethod.invoke(recentStage, 0);
                                    stageSetMethod.invoke(newStage, Integer.parseInt(valueStage));
                                } else if (listType.toString().equals("float") || listType.toString().equals("double")) {
                                    stageSetMethod.invoke(recentStage, 0.0);
                                    stageSetMethod.invoke(newStage, Float.parseFloat(valueStage));
                                } else {
                                    stageSetMethod.invoke(recentStage, "");
                                    stageSetMethod.invoke(newStage, valueStage);
                                }
                            }
                            res += "\n" + methodsStage[i].getName() + "= " + valueStage;
                        } else {
                            if (fobjStage != null && fobjStage.toString().equals("0") && fobjStage.toString().equals("0.0")) {
                                Timber.d(recentStage.getRestId() + "/" + recentStage.getTrackRestId() + " ausgelassen:" + methodsStage[i].getName() +
                                        "() stage/track:" + fobjStage + "/" + fobjStage);
                            }
                        }
                    } else {
                        if (!methodsStage[i].getName().equals("getCreated") &&
                                !methodsStage[i].getName().startsWith("getTrackRestId") &&
                                !methodsStage[i].getName().startsWith("getIns") &&
                                !methodsStage[i].getName().startsWith("getMembersonly") && // TODO sonderfall
                                !methodsStage[i].getName().startsWith("getRaceonly") && // TODO sonderfall
                                !methodsStage[i].getName().startsWith("getUpdated") &&
                                !methodsStage[i].getName().startsWith("getRating") &&
                                !methodsStage[i].getName().startsWith("getSingleTrack") &&
                                !methodsStage[i].getName().equals("getCampingRVhookups")) {
                            Timber.w("gibt es nicht in Tracks:" + methodsStage[i].getName());
                        }
                    }
                }

            } catch (final IllegalArgumentException | IllegalAccessException | InvocationTargetException e) {
                Timber.e(e.getMessage() + " " + methodsStage[i].getName());
            }
        }
        if (cleared) {
            newStage.setCreated(System.currentTimeMillis() / 1000);
            recentStage.setCreated(System.currentTimeMillis() / 1000);
            recentStage.setUpdated(1);
            newStage.setApproved(0);
            newStage.setAndroidid(recentStage.getAndroidid());
            newStage.save(false);
            recentStage.save(false);
        }
        return res;
    }

    private static Method getMethode(Method[] methods, String name) {
        Method res = null;
        for (final Method method : methods) {
            if (method.getName().toLowerCase().equals(name.toLowerCase())) {
                res = method;
            }
        }
        return res;
    }

}
