package igrek.todotree.services.filesystem;

import android.app.Activity;
import android.os.Build;
import android.os.Environment;

import java.io.File;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Locale;

import igrek.todotree.logger.Logs;

/**
 * service to find external sd card location (it's not so obvious)
 */
public class ExternalCardService {
	
	private Logs logger;
	private Activity activity;
	
	private String externalSDPath;
	
	public ExternalCardService(Logs logger, Activity activity) {
		this.logger = logger;
		this.activity = activity;
		externalSDPath = findExternalSDPath();
		logger.debug("External SD Card path: " + externalSDPath);
		//		logger.debug("DEVICE = " + android.os.Build.DEVICE);
		//		logger.debug("MANUFACTURER = " + android.os.Build.MANUFACTURER);
	}
	
	private String findExternalSDPath() {
		return new FirstFinder<String>().addRule(this::isSamsung, checkDirExists("/storage/extSdCard"))
				.addRule(getExternalMount())
				.addRule(checkDirExists("/storage/extSdCard"))
				.addRule(checkDirExists("/storage/external_sd"))
				.addRule(checkDirExists("/storage/ext_sd"))
				.addRule(checkDirExists("/storage/external"))
				.addRule(Environment.getExternalStorageDirectory().getAbsolutePath())
				.find();
	}
	
	private String checkDirExists(String path) {
		File f = new File(path);
		if (f.exists() && f.isDirectory())
			return path;
		return null;
	}
	
	private boolean isSamsung() {
		return Build.DEVICE.contains("samsung") || Build.MANUFACTURER.contains("samsung");
	}
	
	private String getExternalMount() {
		HashSet<String> externalMounts = getExternalMounts();
		if (externalMounts.size() > 1) {
			logger.warn("multiple external mounts found, getting the first one");
		}
		if (!externalMounts.isEmpty())
			return externalMounts.iterator().next();
		return null;
	}
	
	private HashSet<String> getExternalMounts() {
		final HashSet<String> out = new HashSet<>();
		String reg = "(?i).*vold.*(vfat|ntfs|exfat|fat32|ext3|ext4).*rw.*";
		String s = "";
		try {
			final Process process = new ProcessBuilder().command("mount")
					.redirectErrorStream(true)
					.start();
			process.waitFor();
			final InputStream is = process.getInputStream();
			final byte[] buffer = new byte[1024];
			while (is.read(buffer) != -1) {
				s = s + new String(buffer);
			}
			is.close();
		} catch (final Exception e) {
			e.printStackTrace();
		}
		
		// parse output
		final String[] lines = s.split("\n");
		for (String line : lines) {
			if (!line.toLowerCase(Locale.US).contains("asec")) {
				if (line.matches(reg)) {
					String[] parts = line.split(" ");
					for (String part : parts) {
						if (part.startsWith("/"))
							if (!part.toLowerCase(Locale.US).contains("vold")) {
								// lg workaround - brilliant
								if (android.os.Build.MANUFACTURER.contains("LGE"))
									part = part.replaceAll("^/mnt/media_rw", "/storage");
								out.add(part);
							}
					}
				}
			}
		}
		return out;
	}
	
}
