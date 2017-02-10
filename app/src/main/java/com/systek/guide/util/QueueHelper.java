package com.systek.guide.util;

import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaSessionCompat;

import com.systek.guide.biz.MusicProvider;

import java.util.ArrayList;
import java.util.List;

import static com.systek.guide.util.MediaIDHelper.MEDIA_ID_MUSEUM_ID;
import static com.systek.guide.util.MediaIDHelper.MEDIA_ID_MUSICS_BY_SEARCH;

/**
 * Created by Qiang on 2016/8/2.
 *
 * Utility class to help on queue related tasks.
 */
public class QueueHelper {

    private static final String TAG = QueueHelper.class.getSimpleName();

    public static List<MediaSessionCompat.QueueItem> getPlayingQueue(String mediaId,
                                                                     MusicProvider musicProvider) {
        // extract the browsing hierarchy from the media ID:
        String[] hierarchy = MediaIDHelper.getHierarchy(mediaId);
        if (hierarchy.length != 2) {
            LogUtil.e(TAG, "Could not build a playing queue for this mediaId: "+ mediaId);
            return null;
        }
        String categoryType = hierarchy[0];
        String categoryValue = hierarchy[1];
        LogUtil.d(TAG, "Creating playing queue for "+ categoryType + ",  "+ categoryValue);
        Iterable<MediaMetadataCompat> tracks = null;
        // This sample only supports genre and by_search category types.
        if (categoryType.equals(MEDIA_ID_MUSEUM_ID)) {
            tracks = musicProvider.getMusicsByMuseumId(categoryValue);
        } else if (categoryType.equals(MEDIA_ID_MUSICS_BY_SEARCH)) {
            tracks = musicProvider.searchMusicBySongTitle(categoryValue);
        }
        if (tracks == null) {
            return null;
        }
        return convertToQueue(tracks, hierarchy[0], hierarchy[1]);
    }

    public static int getMusicIndexOnQueue(Iterable<MediaSessionCompat.QueueItem> queue,
                                           String mediaId) {
        int index = 0;
        for (MediaSessionCompat.QueueItem item : queue) {
            if (mediaId.equals(item.getDescription().getMediaId())) {
                return index;
            }
            index++;
        }
        return -1;
    }

    public static int getMusicIndexOnQueue(Iterable<MediaSessionCompat.QueueItem> queue,
                                           long queueId) {
        int index = 0;
        for (MediaSessionCompat.QueueItem item : queue) {
            if (queueId == item.getQueueId()) {
                return index;
            }
            index++;
        }
        return -1;
    }

    private static List<MediaSessionCompat.QueueItem> convertToQueue(
            Iterable<MediaMetadataCompat> tracks, String... categories) {
        List<MediaSessionCompat.QueueItem> queue = new ArrayList<>();
        int count = 0;
        for (MediaMetadataCompat track : tracks) {

            // We create a hierarchy-aware mediaID, so we know what the queue is about by looking
            // at the QueueItem media IDs.
            String hierarchyAwareMediaID = MediaIDHelper.createMediaID(
                    track.getDescription().getMediaId(), categories);

            MediaMetadataCompat trackCopy = new MediaMetadataCompat.Builder(track)
                    .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, hierarchyAwareMediaID)
                    .build();

            // We don't expect queues to change after created, so we use the item index as the
            // queueId. Any other number unique in the queue would work.
            MediaSessionCompat.QueueItem item = new MediaSessionCompat.QueueItem(
                    trackCopy.getDescription(), count++);
            queue.add(item);
        }
        return queue;

    }

   /* *
     * Create a random queue.
     *
     * @param musicProvider the provider used for fetching music.
     * @return list containing {@link MediaSession.QueueItem}'s
     *//*
    public static List<MediaSessionCompat.QueueItem> getRandomQueue(MusicProvider musicProvider) {
        List<MediaMetadataCompat> result = new ArrayList<>();

        for (String museumId: musicProvider.getMuseumIds()) {
            Iterable<MediaMetadataCompat> tracks = musicProvider.getMusicsByMuseumId(museumId);
            for (MediaMetadataCompat track: tracks) {
                if (ThreadLocalRandom.current().nextBoolean()) {
                    result.add(track);
                }
            }
        }
        LogUtil.d(TAG, "getRandomQueue: result.size="+ result.size());

        Collections.shuffle(result);

        return convertToQueue(result, MEDIA_ID_MUSICS_BY_SEARCH ,"random");
    }*/

    public static boolean isIndexPlayable(int index, List<MediaSessionCompat.QueueItem> queue) {
        return (queue != null && index >= 0 && index < queue.size());
    }
    
}
