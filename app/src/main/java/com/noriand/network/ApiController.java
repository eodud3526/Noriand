package com.noriand.network;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.noriand.activity.BaseActivity;
import com.noriand.common.SLog;
import com.noriand.constant.ServerConstant;
import com.noriand.util.MultipartUtil;
import com.noriand.vo.request.RequestDeleteDeviceVO;
import com.noriand.vo.request.RequestDeleteSafeZoneVO;
import com.noriand.vo.request.RequestGetAlarmArrayVO;
import com.noriand.vo.request.RequestGetDeviceArrayVO;
import com.noriand.vo.request.RequestGetDeviceLocationVO;
import com.noriand.vo.request.RequestGetNowLocationVO;
import com.noriand.vo.request.RequestGetSafeZoneArrayVO;
import com.noriand.vo.request.RequestGetTraceArrayVO;
import com.noriand.vo.request.RequestJoinVO;
import com.noriand.vo.request.RequestLoginVO;
import com.noriand.vo.request.RequestUpdateDeviceSettingVO;
import com.noriand.vo.request.RequestUploadFileVO;
import com.noriand.vo.request.RequestWriteDeviceVO;
import com.noriand.vo.request.RequestWriteSafeZoneVO;
import com.noriand.vo.response.ResponseGetAlarmArrayVO;
import com.noriand.vo.response.ResponseGetDeviceArrayVO;
import com.noriand.vo.response.ResponseGetDeviceLocationVO;
import com.noriand.vo.response.ResponseGetNowLocationVO;
import com.noriand.vo.response.ResponseGetSafeZoneArrayVO;
import com.noriand.vo.response.ResponseGetTraceArrayVO;
import com.noriand.vo.response.ResponseJoinVO;
import com.noriand.vo.response.ResponseLoginVO;
import com.noriand.vo.response.ResponseUploadFileVO;
import com.noriand.vo.response.ResponseVO;
import com.noriand.vo.response.ResponseWriteDeviceVO;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;


public class ApiController {
	private final String TAG = this.getClass().getSimpleName();

	private final int TIME_OUT = 10000;
	private final String CHAR_SET = "utf-8";

	public boolean isApiLoading = false;

	public boolean checkNetwork(Context context) {
		ConnectivityManager connectivityManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
		if(networkInfo != null) {
			if(networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
				SLog.d(TAG, "(checkNetwork) TYPE_WIFI");
				return true;
			} else if(networkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
				SLog.d(TAG, "(checkNetwork) TYPE_MOBILE");
				return true;
			}
		}
        return false;
	}

	public void login(final BaseActivity activity, final RequestLoginVO requestItem, final ApiLoginListener listener) {
		if(!checkNetwork(activity.getApplicationContext())) {
			listener.onFail();
			return;
		}

		String requestUrl = ServerConstant.getUrl(ServerConstant.APP_URL);
		String param = requestItem.getParameter();

		if(!requestItem.isSilent) {
			activity.startProgress();
		}
		requestPost(requestUrl, param, new NetworkFinishListener() {
			@Override
			public void onFinish(String result) {
				ResponseLoginVO item = null;
				try {
					JSONObject jsonObject = new JSONObject(result);
					item = new ResponseLoginVO();
					item.parseJSONObject(jsonObject);
				} catch (JSONException e) {
					activity.runOnUiThread(new Runnable() {
						@Override
						public void run() {
							activity.endProgress();
							listener.onFail();
						}
					});
					return;
				}

				final ResponseLoginVO responseItem = item;
				activity.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						activity.endProgress();
						listener.onSuccess(responseItem);
					}
				});
			}
		});
	}

	public void join(final BaseActivity activity, final RequestJoinVO requestItem, final ApiJoinListener listener) {
		if(!checkNetwork(activity.getApplicationContext())) {
			listener.onFail();
			return;
		}

		String requestUrl = ServerConstant.getUrl(ServerConstant.APP_URL);
		String param = requestItem.getParameter();

		if(!requestItem.isSilent) {
			activity.startProgress();
		}
		requestPost(requestUrl, param, new NetworkFinishListener() {
			@Override
			public void onFinish(String result) {
				ResponseJoinVO item = null;
				try {
					JSONObject jsonObject = new JSONObject(result);
					item = new ResponseJoinVO();
					item.parseJSONObject(jsonObject);
				} catch (JSONException e) {
					activity.runOnUiThread(new Runnable() {
						@Override
						public void run() {
							activity.endProgress();
							listener.onFail();
						}
					});
					return;
				}

				final ResponseJoinVO responseItem = item;
				activity.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						activity.endProgress();
						listener.onSuccess(responseItem);
					}
				});
			}
		});
	}

	public void getDeviceLocation(final BaseActivity activity, final RequestGetDeviceLocationVO requestItem, final ApiGetDeviceLocationListener listener) {
		if(!checkNetwork(activity.getApplicationContext())) {
			listener.onFail();
			return;
		}

		String requestUrl = ServerConstant.getUrl(ServerConstant.APP_URL);
		String param = requestItem.getParameter();

		if(!requestItem.isSilent) {
			activity.startProgress();
		}
		requestPost(requestUrl, param, new NetworkFinishListener() {
			@Override
			public void onFinish(String result) {
				ResponseGetDeviceLocationVO item = null;
				try {
					JSONObject jsonObject = new JSONObject(result);
					item = new ResponseGetDeviceLocationVO();
					item.parseJSONObject(jsonObject);
				} catch (JSONException e) {
					activity.runOnUiThread(new Runnable() {
						@Override
						public void run() {
							activity.endProgress();
							listener.onFail();
						}
					});
					return;
				}

				final ResponseGetDeviceLocationVO responseItem = item;
				activity.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						activity.endProgress();
						listener.onSuccess(responseItem);
					}
				});
			}
		});
	}

	public void uploadFile(final BaseActivity activity, final RequestUploadFileVO requestItem, final ApiUploadFileListener listener) {
		if(!checkNetwork(activity.getApplicationContext())) {
			listener.onFail();
			return;
		}

		String requestUrl = ServerConstant.getUrl(ServerConstant.FILE_URL);
		String kind = requestItem.getKind();
		String filePath = requestItem.filePath;

		String[] fileNameArray = {"file"};
		String[] filePathArray = {filePath};

		String[] paramNameArray = requestItem.getParamNameArray();
		String[] paramContentArray = requestItem.getParamContentArray();

		activity.startProgress();
		requestMultipart(requestUrl, kind, paramNameArray, paramContentArray, fileNameArray, filePathArray, new NetworkFinishListener() {
			@Override
			public void onFinish(String result) {
				ResponseUploadFileVO item = null;
				try {
					JSONObject jsonObject = new JSONObject(result);
					item = new ResponseUploadFileVO();
					item.parseJSONObject(jsonObject);
				} catch (JSONException e) {
					activity.runOnUiThread(new Runnable() {
						@Override
						public void run() {
							activity.endProgress();
							listener.onFail();
						}
					});
					return;
				}

				final ResponseUploadFileVO responseItem = item;
				activity.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						activity.endProgress();
						listener.onSuccess(responseItem);
					}
				});
			}
		});
	}

	public void getDeviceArray(final BaseActivity activity, final RequestGetDeviceArrayVO requestItem, final ApiGetDeviceArrayListener listener) {
		if(!checkNetwork(activity.getApplicationContext())) {
			listener.onFail();
			return;
		}

		String requestUrl = ServerConstant.getUrl(ServerConstant.APP_URL);
		String param = requestItem.getParameter();

		if(!requestItem.isSilent) {
			activity.startProgress();
		}
		requestPost(requestUrl, param, new NetworkFinishListener() {
			@Override
			public void onFinish(String result) {
				ResponseGetDeviceArrayVO item = null;
				try {
					JSONObject jsonObject = new JSONObject(result);
					item = new ResponseGetDeviceArrayVO();
					item.parseJSONObject(jsonObject);
				} catch (JSONException e) {
					activity.runOnUiThread(new Runnable() {
						@Override
						public void run() {
							activity.endProgress();
							listener.onFail();
						}
					});
					return;
				}

				final ResponseGetDeviceArrayVO responseItem = item;
				activity.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						activity.endProgress();
						listener.onSuccess(responseItem);
					}
				});
			}
		});
	}

	public void updateDeviceSetting(final BaseActivity activity, final RequestUpdateDeviceSettingVO requestItem, final ApiCommonListener listener) {
		if(!checkNetwork(activity.getApplicationContext())) {
			listener.onFail();
			return;
		}

		String requestUrl = ServerConstant.getUrl(ServerConstant.APP_URL);
		String param = requestItem.getParameter();

		if(!requestItem.isSilent) {
			activity.startProgress();
		}
		requestPost(requestUrl, param, new NetworkFinishListener() {
			@Override
			public void onFinish(String result) {
				ResponseVO item = null;
				try {
					JSONObject jsonObject = new JSONObject(result);
					item = new ResponseVO();
					item.parseJSONObject(jsonObject);
				} catch (JSONException e) {
					activity.runOnUiThread(new Runnable() {
						@Override
						public void run() {
							activity.endProgress();
							listener.onFail();
						}
					});
					return;
				}

				final ResponseVO responseItem = item;
				activity.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						activity.endProgress();
						listener.onSuccess(responseItem);
					}
				});
			}
		});
	}


	public void getTraceArray(final BaseActivity activity, final RequestGetTraceArrayVO requestItem, final ApiGetTraceArrayListener listener) {
		if(!checkNetwork(activity.getApplicationContext())) {
			listener.onFail();
			return;
		}

		String requestUrl = ServerConstant.getUrl(ServerConstant.APP_URL);
		String param = requestItem.getParameter();

		if(!requestItem.isSilent) {
			activity.startProgress();
		}
		requestPost(requestUrl, param, new NetworkFinishListener() {
			@Override
			public void onFinish(String result) {
				ResponseGetTraceArrayVO item = null;
				try {
					JSONObject jsonObject = new JSONObject(result);
					item = new ResponseGetTraceArrayVO();
					item.parseJSONObject(jsonObject);
				} catch (JSONException e) {
					activity.runOnUiThread(new Runnable() {
						@Override
						public void run() {
							activity.endProgress();
							listener.onFail();
						}
					});
					return;
				}

				final ResponseGetTraceArrayVO responseItem = item;
				activity.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						activity.endProgress();
						listener.onSuccess(responseItem);
					}
				});
			}
		});
	}


	public void getNowLocation(final BaseActivity activity, final RequestGetNowLocationVO requestItem, final ApiGetNowLocationListener listener) {
		if(!checkNetwork(activity.getApplicationContext())) {
			listener.onFail();
			return;
		}

		String requestUrl = ServerConstant.getUrl(ServerConstant.APP_URL);
		String param = requestItem.getParameter();

		if(!requestItem.isSilent) {
			activity.startProgress();
		}
		requestPost(requestUrl, param, new NetworkFinishListener() {
			@Override
			public void onFinish(String result) {
				ResponseGetNowLocationVO item = null;
				try {
					JSONObject jsonObject = new JSONObject(result);
					item = new ResponseGetNowLocationVO();
					item.parseJSONObject(jsonObject);
				} catch (JSONException e) {
					activity.runOnUiThread(new Runnable() {
						@Override
						public void run() {
							activity.endProgress();
							listener.onFail();
						}
					});
					return;
				}

				final ResponseGetNowLocationVO responseItem = item;
				activity.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						activity.endProgress();
						listener.onSuccess(responseItem);
					}
				});
			}
		});
	}



	public void getAlarmArray(final BaseActivity activity, final RequestGetAlarmArrayVO requestItem, final ApiGetAlarmArrayListener listener) {
		if(!checkNetwork(activity.getApplicationContext())) {
			listener.onFail();
			return;
		}

		String requestUrl = ServerConstant.getUrl(ServerConstant.APP_URL);
		String param = requestItem.getParameter();

		if(!requestItem.isSilent) {
			activity.startProgress();
		}
		requestPost(requestUrl, param, new NetworkFinishListener() {
			@Override
			public void onFinish(String result) {
				ResponseGetAlarmArrayVO item = null;
				try {
					JSONObject jsonObject = new JSONObject(result);
					item = new ResponseGetAlarmArrayVO();
					item.parseJSONObject(jsonObject);
				} catch (JSONException e) {
					activity.runOnUiThread(new Runnable() {
						@Override
						public void run() {
							activity.endProgress();
							listener.onFail();
						}
					});
					return;
				}

				final ResponseGetAlarmArrayVO responseItem = item;
				activity.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						activity.endProgress();
						listener.onSuccess(responseItem);
					}
				});
			}
		});
	}







	public void getSafeZoneArray(final BaseActivity activity, final RequestGetSafeZoneArrayVO requestItem, final ApiGetSafeZoneArrayListener listener) {
		if(!checkNetwork(activity.getApplicationContext())) {
			listener.onFail();
			return;
		}

		String requestUrl = ServerConstant.getUrl(ServerConstant.APP_URL);
		String param = requestItem.getParameter();

		if(!requestItem.isSilent) {
			activity.startProgress();
		}
		requestPost(requestUrl, param, new NetworkFinishListener() {
			@Override
			public void onFinish(String result) {
				ResponseGetSafeZoneArrayVO item = null;
				try {
					JSONObject jsonObject = new JSONObject(result);
					item = new ResponseGetSafeZoneArrayVO();
					item.parseJSONObject(jsonObject);
				} catch (JSONException e) {
					activity.runOnUiThread(new Runnable() {
						@Override
						public void run() {
							activity.endProgress();
							listener.onFail();
						}
					});
					return;
				}

				final ResponseGetSafeZoneArrayVO responseItem = item;
				activity.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						activity.endProgress();
						listener.onSuccess(responseItem);
					}
				});
			}
		});
	}

	public void writeSafeZone(final BaseActivity activity, final RequestWriteSafeZoneVO requestItem, final ApiCommonListener listener) {
		if(!checkNetwork(activity.getApplicationContext())) {
			listener.onFail();
			return;
		}

		String requestUrl = ServerConstant.getUrl(ServerConstant.APP_URL);
		String param = requestItem.getParameter();

		if(!requestItem.isSilent) {
			activity.startProgress();
		}
		requestPost(requestUrl, param, new NetworkFinishListener() {
			@Override
			public void onFinish(String result) {
				ResponseVO item = null;
				try {
					JSONObject jsonObject = new JSONObject(result);
					item = new ResponseVO();
					item.parseJSONObject(jsonObject);
				} catch (JSONException e) {
					activity.runOnUiThread(new Runnable() {
						@Override
						public void run() {
							activity.endProgress();
							listener.onFail();
						}
					});
					return;
				}

				final ResponseVO responseItem = item;
				activity.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						activity.endProgress();
						listener.onSuccess(responseItem);
					}
				});
			}
		});
	}


	public void deleteSafeZone(final BaseActivity activity, final RequestDeleteSafeZoneVO requestItem, final ApiCommonListener listener) {
		if(!checkNetwork(activity.getApplicationContext())) {
			listener.onFail();
			return;
		}

		String requestUrl = ServerConstant.getUrl(ServerConstant.APP_URL);
		String param = requestItem.getParameter();

		if(!requestItem.isSilent) {
			activity.startProgress();
		}
		requestPost(requestUrl, param, new NetworkFinishListener() {
			@Override
			public void onFinish(String result) {
				ResponseVO item = null;
				try {
					JSONObject jsonObject = new JSONObject(result);
					item = new ResponseVO();
					item.parseJSONObject(jsonObject);
				} catch (JSONException e) {
					activity.runOnUiThread(new Runnable() {
						@Override
						public void run() {
							activity.endProgress();
							listener.onFail();
						}
					});
					return;
				}

				final ResponseVO responseItem = item;
				activity.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						activity.endProgress();
						listener.onSuccess(responseItem);
					}
				});
			}
		});
	}


	public void writeDevice(final BaseActivity activity, final RequestWriteDeviceVO requestItem, final ApiWriteDeviceListener listener) {
		if(!checkNetwork(activity.getApplicationContext())) {
			listener.onFail();
			return;
		}

		String requestUrl = ServerConstant.getUrl(ServerConstant.APP_URL);
		String param = requestItem.getParameter();

		if(!requestItem.isSilent) {
			activity.startProgress();
		}
		requestPost(requestUrl, param, new NetworkFinishListener() {
			@Override
			public void onFinish(String result) {
				ResponseWriteDeviceVO item = null;
				try {
					JSONObject jsonObject = new JSONObject(result);
					item = new ResponseWriteDeviceVO();
					item.parseJSONObject(jsonObject);
				} catch (JSONException e) {
					activity.runOnUiThread(new Runnable() {
						@Override
						public void run() {
							activity.endProgress();
							listener.onFail();
						}
					});
					return;
				}

				final ResponseWriteDeviceVO responseItem = item;
				activity.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						activity.endProgress();
						listener.onSuccess(responseItem);
					}
				});
			}
		});
	}

	public void deleteDevice(final BaseActivity activity, final RequestDeleteDeviceVO requestItem, final ApiCommonListener listener) {
		if(!checkNetwork(activity.getApplicationContext())) {
			listener.onFail();
			return;
		}

		String requestUrl = ServerConstant.getUrl(ServerConstant.APP_URL);
		String param = requestItem.getParameter();

		if(!requestItem.isSilent) {
			activity.startProgress();
		}
		requestPost(requestUrl, param, new NetworkFinishListener() {
			@Override
			public void onFinish(String result) {
				ResponseVO item = null;
				try {
					JSONObject jsonObject = new JSONObject(result);
					item = new ResponseVO();
					item.parseJSONObject(jsonObject);
				} catch (JSONException e) {
					activity.runOnUiThread(new Runnable() {
						@Override
						public void run() {
							activity.endProgress();
							listener.onFail();
						}
					});
					return;
				}

				final ResponseVO responseItem = item;
				activity.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						activity.endProgress();
						listener.onSuccess(responseItem);
					}
				});
			}
		});
	}



	public void requestGet(final String requestUrl, final String[] headerNameArray, final String[] headerContentArray, final NetworkFinishListener listener) {
		SLog.d(TAG, "(requestGet) requestUrl: " + requestUrl);
		new Thread(new Runnable() {
			@Override
			public void run() {
				isApiLoading = true;
				
				String response = "";
				HttpURLConnection conn = null;
				BufferedReader br = null;
				try {
					URL url = new URL(requestUrl);
					conn = (HttpURLConnection)url.openConnection();
					conn.setRequestMethod("GET");
					
					if(headerContentArray != null && headerNameArray != null) {
						int length = headerContentArray.length;
						for(int i=0; i<length; i++) {
							conn.setRequestProperty(headerNameArray[i], headerContentArray[i]);
						}
					}

					StringBuilder sb = new StringBuilder();
					String line = "";
					br = new BufferedReader(new InputStreamReader(conn.getInputStream(), CHAR_SET));
					while ((line = br.readLine()) != null) {
						sb.append(line);
					}
					SLog.d(TAG, "(requestGet) responseCode: " + conn.getResponseCode());
					response = sb.toString();
				} catch(IOException e) {
					SLog.d(TAG, "(requestGet) e: " + e.toString());
				} finally {
					isApiLoading = false;
					if(br != null) {
						try {
							br.close();
						} catch (IOException e) {
						} 
					}
				}
				
				SLog.d(TAG, "(requestGet) response: " + response);
				listener.onFinish(response);
			}
		}).start();
	}

	public void requestPost(String requestUrl, String param, NetworkFinishListener listener) {
		requestPost(requestUrl, null, null, param, listener);
	}

	public void requestPost(final String requestUrl, final String[] headerNameArray, final String[] headerContentArray,
			final String param, final NetworkFinishListener listener) {
		SLog.d(TAG, "(requestPost) requestUrl: " + requestUrl);
		SLog.d(TAG, "(requestPost) param: " + param);

		new Thread(new Runnable() {
			@Override
			public void run() {
				isApiLoading = true;

				String response = "";
				HttpURLConnection conn = null;
				OutputStream os = null;

				try {
					URL url = new URL(requestUrl);
					conn = (HttpURLConnection)url.openConnection();
					conn.setDefaultUseCaches(false);
					conn.setDoInput(true);
					conn.setDoOutput(true);
					conn.setConnectTimeout(TIME_OUT);
					conn.setReadTimeout(TIME_OUT);
					conn.setRequestMethod("POST");
					conn.setRequestProperty("content-type", "application/x-www-form-urlencoded");
					if(headerNameArray != null && headerContentArray != null) {
					int headerSize = headerNameArray.length;
						for(int i=0; i<headerSize; i++) {
							String name = headerNameArray[i];
							String content = headerContentArray[i];
							SLog.d(TAG, "name: " + name + ", content: " + content);
							conn.setRequestProperty(name, content);
						}
					}

					PrintWriter pw = new PrintWriter(new OutputStreamWriter(conn.getOutputStream(), CHAR_SET));
					pw.write(param);
					pw.flush();

					int responseCode = conn.getResponseCode();
					SLog.d(TAG, "responseCode: " + responseCode);
					if(responseCode == HttpsURLConnection.HTTP_OK) {
						StringBuilder sb = new StringBuilder();
						String line = "";
						BufferedReader br = new BufferedReader(
								new InputStreamReader(conn.getInputStream(), CHAR_SET));
						while((line = br.readLine()) != null) {
							sb.append(line);
						}
						response = sb.toString();
					} else {
						response = "";
					}
				} catch (IOException e) {
				} finally {
					isApiLoading = false;
					if(os != null) {
						try {
							os.close();
						} catch(IOException e1) {
						}
					}
					if (conn != null) {
						conn.disconnect();
					}
				}

				SLog.d(TAG, "(requestPost) response: " + response);
				listener.onFinish(response);
			}
		}).start();
	}

	public void requestMultipart(final String requestUrl, final String kind, final String[] paramNameArray, final String[] paramContentArray, final String[] fileNameArray, final String[] filePathArray, final NetworkFinishListener listener) {
		SLog.d(TAG, "(requestMultipart) requestUrl: " + requestUrl);

		new Thread(new Runnable() {
			@Override
			public void run() {
				isApiLoading = true;
				String result = "";
				
				String mimeType = "binary/octet-stream";
				//String mimeType = "image/jpeg";

				MultipartUtil multipart = null;
				try {
					multipart = new MultipartUtil(requestUrl, CHAR_SET, kind);
					if(paramNameArray != null && paramContentArray != null) {
						int paramLength = paramNameArray.length;
						if(paramLength > 0) {
							for(int i=0; i<paramLength; i++) {
								multipart.addFormField(paramNameArray[i], paramContentArray[i]);
							}
						}
					}

					if(filePathArray != null) {
						int filePathLength = filePathArray.length;
						for(int i=0; i<filePathLength; i++) {
							File file = new File(filePathArray[i]);
							multipart.addFilePart(fileNameArray[i], file, mimeType);
						}
					}

					ArrayList<String> response = multipart.finish();
					if(response != null) {
						int size = response.size();
						for (int i=0; i<size; i++) {
							result += response.get(i);
						}
					}
				} catch(IOException ioe) {
				} finally {
					SLog.d(TAG, "(requestMultipart) result: " + result);
					isApiLoading = false;
					listener.onFinish(result);
				}
			}
		}).start();
	}




	public interface ApiCommonListener {
		void onSuccess(ResponseVO item);
		void onFail();
	}

	public interface NetworkFinishListener {
		void onFinish(String result);
	}

	public interface ApiGetNowLocationListener {
		void onSuccess(ResponseGetNowLocationVO item);
		void onFail();
	}

	public interface ApiJoinListener {
		void onSuccess(ResponseJoinVO item);
		void onFail();
	}

	public interface ApiLoginListener {
		void onSuccess(ResponseLoginVO item);
		void onFail();
	}

	public interface ApiUploadFileListener {
		void onSuccess(ResponseUploadFileVO item);
		void onFail();
	}

	public interface ApiGetDeviceArrayListener {
		void onSuccess(ResponseGetDeviceArrayVO item);
		void onFail();
	}

	public interface ApiGetTraceArrayListener {
		void onSuccess(ResponseGetTraceArrayVO item);
		void onFail();
	}

	public interface ApiGetAlarmArrayListener {
		void onSuccess(ResponseGetAlarmArrayVO item);
		void onFail();
	}

	public interface ApiGetSafeZoneArrayListener {
		void onSuccess(ResponseGetSafeZoneArrayVO item);
		void onFail();
	}

	public interface ApiGetDeviceLocationListener {
		void onSuccess(ResponseGetDeviceLocationVO item);
		void onFail();
	}

	public interface ApiWriteDeviceListener {
		void onSuccess(ResponseWriteDeviceVO item);
		void onFail();
	}
}