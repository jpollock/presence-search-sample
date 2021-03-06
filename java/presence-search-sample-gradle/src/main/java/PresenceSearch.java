import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.pubnub.api.PNConfiguration;
import com.pubnub.api.PubNub;
import com.pubnub.api.PubNubException;
import com.pubnub.api.models.consumer.presence.PNHereNowChannelData;
import com.pubnub.api.models.consumer.presence.PNHereNowOccupantData;
import com.pubnub.api.models.consumer.presence.PNHereNowResult;

public class PresenceSearch {
	static PubNub pubnub;
	static int limit = 100;
	
	private ArrayList<PNHereNowOccupantData> baseHereNowGetAllPages(ArrayList channels, boolean includeUuids, boolean includeState, Map<String, String> queryParams) {
		ArrayList<PNHereNowOccupantData> occupants = new ArrayList<PNHereNowOccupantData>();
		// this method will iterate over channels
		
		try {
			int totalCount = -1;
			int offset = 0;
						
			while (totalCount == -1 || offset < totalCount) {
				queryParams.put("offset", Integer.toString(offset));
				PNHereNowResult response = pubnub.hereNow()
						.channels(channels)
						.includeUUIDs(includeUuids)
						.includeState(includeState)
						.queryParam(queryParams)
						.sync();
						
	            for (PNHereNowChannelData channelData : response.getChannels().values()) {
	                for (PNHereNowOccupantData  occupant : channelData.getOccupants()) {
	                	occupants.add(occupant);
		                offset++;	                	
	                }	                
	            }
	            totalCount = response.getTotalOccupancy();
			}
			
		} catch (PubNubException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		
		return occupants;
	}
	
	
	public void hereNowShowUuidsNoStateSortDesc(ArrayList channels) {
        Map<String, String> queryParams = new HashMap<>();
        queryParams.put("limit", Integer.toString(limit));
        queryParams.put("sort", "value:desc");

        ArrayList<PNHereNowOccupantData> occupants = baseHereNowGetAllPages(channels, true, false, queryParams);
		System.out.println(occupants.size());
	}
	public void hereNowShowUuidsShowStateSortDesc(ArrayList channels) {
        Map<String, String> queryParams = new HashMap<>();
        queryParams.put("limit", Integer.toString(limit));
        queryParams.put("sort", "value:desc");

        ArrayList<PNHereNowOccupantData> occupants = baseHereNowGetAllPages(channels, true, true, queryParams);
		System.out.println(occupants.size());
	}

	public void hereNowShowUuidsShowStateSortDescSingleFilter(ArrayList channels) {
        Map<String, String> queryParams = new HashMap<>();
        queryParams.put("limit", Integer.toString(limit));
        queryParams.put("sort", "value:desc");
        queryParams.put("filter", "value:gte:150");

        ArrayList<PNHereNowOccupantData> occupants = baseHereNowGetAllPages(channels, true, true, queryParams);
		System.out.println(occupants.size());		
	}
	
	public void hereNowShowUuidsShowStateSortDescDoubleFilter(ArrayList channels) {
        Map<String, String> queryParams = new HashMap<>();
        queryParams.put("limit", Integer.toString(limit));
        queryParams.put("sort", "value:desc");
        queryParams.put("filter", "value:gte:150,value:lte:160");

        ArrayList<PNHereNowOccupantData> occupants = baseHereNowGetAllPages(channels, true, true, queryParams);
		System.out.println(occupants.size());		
	}

	public static void main(String[] args) {
		if (args.length < 1 || args.length > 3) {
			System.out.println("Usage: gradle run --args '<sub key> <channel (comma delimited for multiple)> <optional: origin>");
			System.out.println("\t\tExample: gradle run --args 'myreallylongsubkeystring mychannel1,mychannel2'");
			System.out.println("\t\tExample: gradle run --args 'myreallylongsubkeystring mychannel1,mychannel2 mycustom.pubnub.com'");
			System.exit(0);
		}
		PNConfiguration pnConfiguration = new PNConfiguration();
		pnConfiguration.setSecure(false);
		pnConfiguration.setSubscribeKey(args[0]);
		if (args.length == 3) {
			pnConfiguration.setOrigin(args[2]);	
		}
		
		String channels = args[1];
		
		pubnub = new PubNub(pnConfiguration);
		
		PresenceSearch app = new PresenceSearch();
		;

		ArrayList channelsList= new ArrayList(Arrays.asList(channels.split(",")));
		
		app.hereNowShowUuidsNoStateSortDesc(channelsList);
		app.hereNowShowUuidsShowStateSortDesc(channelsList);
		app.hereNowShowUuidsShowStateSortDescSingleFilter(channelsList);
		app.hereNowShowUuidsShowStateSortDescDoubleFilter(channelsList);
		

	}

}