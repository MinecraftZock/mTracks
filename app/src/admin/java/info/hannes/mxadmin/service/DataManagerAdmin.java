package info.hannes.mxadmin.service;

import android.content.Intent;

import com.robotoworks.mechanoid.db.SQuery;
import com.robotoworks.mechanoid.ops.Ops;

import java.util.List;

import info.hannes.retrofit.ApiAdminClient;
import info.hannes.retrofit.service.model.StatusResponse;
import info.hannes.retrofit.service.model.Tracksarchiv;
import info.hannes.retrofit.service.model.VersionInfo;
import info.mx.comAdminlib.retrofit.service.model.ApproveResponse;
import info.mx.comAdminlib.retrofit.service.model.Approved;
import info.mx.comlib.retrofit.service.data.Data;
import info.mx.comlib.retrofit.service.data.RxNetworkProblem;
import info.mx.tracks.MxAccessApplication;
import info.mx.core.ops.OpPostImagesOperation;
import info.mx.core_generated.sqlite.MxInfoDBContract;
import info.mx.core_generated.sqlite.PicturesRecord;
import io.reactivex.Observable;
import io.reactivex.Single;
import retrofit2.Response;

/**
 * It keeps a reference to every helper class and uses them to satisfy the requests coming from the UI.
 * Its methods make extensive use of Rx operators to combine, transform or filter the output coming from the helpers in order to generate the
 * desired output ready for the UI.
 * It returns observables that emit data models.
 */
public class DataManagerAdmin extends DataManagerAdminBase {

    private final ApiAdminClient apiAdminClient;

    public DataManagerAdmin(ApiAdminClient apiAdminClient) {
        this.apiAdminClient = apiAdminClient;
    }

    // ================================================
    // business logic
    // ================================================
    @SuppressWarnings("unchecked")
    public Single<Data<List<RxNetworkProblem>>> getNetworkProblems(long lastTime, boolean forceUpdate, int blockSize) {

        Observable<Data<List<RxNetworkProblem>>> memory = (memoryCache.getObservable(MemoryKeys.KEY_NETWORK)
                .cast(List.class)
                .map(Data::memory));

        final String basic = MxAccessApplication.Companion.getAadhresUBase();
        Observable<Data<List<RxNetworkProblem>>> network = apiAdminClient.getSurveillanceService()
                .getNetworkProblems(lastTime, blockSize, basic)
                .compose(applyObservableSchedulers())
                .doOnNext(records -> memoryCache.put(MemoryKeys.KEY_NETWORK, records, MEMORY_VALIDATION_PERIOD))
                .map(Data::network);

        //we either return the memory data (if valid && !forceUpdate) or the network response only
        return Observable.concat(memory, network)
                .compose(applyObservableSchedulers())
                .filter(fileList -> !forceUpdate || fileList.getSource() == Data.Source.NETWORK).firstOrError();
    }

    @SuppressWarnings("unchecked")
    public Single<Data<List<Tracksarchiv>>> getTracksArchive(long trackRestId, boolean forceUpdate) {
        Observable<Data<List<Tracksarchiv>>> memory = (memoryCache.getObservable(MemoryAdminKeys.KEY_ARCHIVE.name() + trackRestId)
                .cast(List.class)
                .map(Data::memory));

        final String basic = MxAccessApplication.Companion.getAadhresUBase();
        Observable<Data<List<Tracksarchiv>>> network = apiAdminClient.getAdminService()
                .getTracksarchiv(trackRestId, basic)
                .compose(applyObservableSchedulers())
                .doOnNext(records -> memoryCache.put(MemoryAdminKeys.KEY_ARCHIVE.name() + trackRestId, records, MEMORY_VALIDATION_PERIOD))
                .map(Data::network);

        //we either return the memory data (if valid && !forceUpdate) or the network response only
        return Observable.concat(memory, network)
                .compose(applyObservableSchedulers())
                .filter(fileList -> !forceUpdate || fileList.getSource() == Data.Source.NETWORK).firstOrError();
    }

    public Observable<VersionInfo> getBackendInfo() {
        final String basic = MxAccessApplication.Companion.getAadhresUBase();
        return apiAdminClient.getSurveillanceService()
                .getBackendInfo(basic)
                .compose(applyObservableSchedulers());
    }

    public Observable<StatusResponse> rotateImage(long imageRestId) {
        final String basic = MxAccessApplication.Companion.getAadhresUBase();
        return apiAdminClient.getAdminService()
                .rotateImage(imageRestId, basic)
                .compose(applyObservableSchedulers());
    }

    public Single<ApproveResponse> approvePicture(Approved approved) {
        final String basic = MxAccessApplication.Companion.getAadhresUBase();
        return apiAdminClient.getAdminService()
                .approvePicture(approved, basic)
                .compose(applySingleSchedulers())
                .doOnSuccess(approveResponse -> {
                    final PicturesRecord rec = SQuery.newQuery()
                            .expr(MxInfoDBContract.Pictures.REST_ID, SQuery.Op.EQ, approved.getId())
                            .selectFirst(MxInfoDBContract.Pictures.CONTENT_URI);
                    if (rec != null && approved.getStatus() == approveResponse.getStatus()) {
                        rec.setApproved(approved.getStatus());
                        rec.setChanged(rec.getChanged() - 1);// force update from server
                        if (approved.getStatus() == -1) {
                            rec.setDeleted(1);
                        }
                        rec.save();

                        // when we do it, and we make an error, it disappear immediate without chance to fix
                        final Intent intentM = OpPostImagesOperation.newIntent();
                        Ops.execute(intentM);
                    }
                });
    }

    public Single<String> trackStageAccept(long stageRestId) {
        final String basic = MxAccessApplication.Companion.getAadhresUBase();
        return apiAdminClient.getAdminService()
                .trackStageAccept(stageRestId, basic)
                .compose(applySingleSchedulers());
    }

    public Single<String> trackStageDecline(long stageRestId) {
        final String basic = MxAccessApplication.Companion.getAadhresUBase();
        return apiAdminClient.getAdminService()
                .trackStageDecline(stageRestId, basic)
                .compose(applySingleSchedulers());
    }

    public Single<Response<Void>> trackStageDelete(long stageRestId) {
        final String basic = MxAccessApplication.Companion.getAadhresUBase();
        return apiAdminClient.getAdminService()
                .trackStageDelete(stageRestId, basic)
                .compose(applySingleSchedulers());
    }

}
