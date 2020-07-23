package moneybags.tempfly.aesthetic.particle;

import java.util.Random;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Particle.DustOptions;
import org.bukkit.block.data.BlockData;

import moneybags.tempfly.TempFly;
import moneybags.tempfly.fly.FlyHandle;
import moneybags.tempfly.fly.Flyer;
import moneybags.tempfly.util.Console;
import moneybags.tempfly.util.V;
import moneybags.tempfly.util.data.DataBridge.DataValue;

public class Particles {

	private static Class<?> dustOptions = null;
	
	public static void initialize() {
		try {
			dustOptions = Class.forName("org.bukkit.Particle$DustOptions");
		} catch (Exception e) {}
	}
	
	public static boolean oldParticles() {
		String version = Bukkit.getVersion();
		return (version.contains("1.6")) || (version.contains("1.7")) || (version.contains("1.8")) || version.contains("1.9");
	}
	
	public static void play(Location loc, String s) {
		if (!oldParticles()) {
			Particle particle = null;
			try {particle = Particle.valueOf(s.toUpperCase());} catch (Exception e1) {
				try {particle = Particle.valueOf(V.particleType.toUpperCase());} catch (Exception e2) {
					particle = Particle.VILLAGER_HAPPY;
				};
			}
			
			Class<?> c = particle.getDataType();
			try {
				if (dustOptions != null && dustOptions.equals(c)) {
					Random rand = new Random();
					loc.getWorld().spawnParticle(particle, loc, 1, new DustOptions(Color.fromRGB(rand.nextInt(255), rand.nextInt(255), rand.nextInt(255)), 2f));	
				} else if (BlockData.class.equals(c)) {
					loc.getWorld().spawnParticle(particle, loc, 1, Material.STONE.createBlockData());	
				} else {
					loc.getWorld().spawnParticle(particle, loc, 1, 0, 0, 0, 0.1);
				}
			} catch (Exception e) {
				loc.getWorld().spawnParticle(Particle.VILLAGER_HAPPY, loc, 1, 0, 0, 0, 0.1);
			}
		} else {
			Effect particle = null;
			// This effect value crashes clients and prevents them from joining the server again.
			if (s != null && s.equalsIgnoreCase("ITEM_BREAK")) {
				s = "HAPPY_VILLAGER";
			}
			try {particle = Effect.valueOf(s.toUpperCase());} catch (Exception e1) {
				try {particle = Effect.valueOf(V.particleType);} catch (Exception e2) {
					particle = Effect.valueOf("HAPPY_VILLAGER");	
				}
			}
			loc.getWorld().playEffect(loc, particle, 1);
		}
	}
	
	public static String loadTrail(UUID u) {
		String particle = (String) TempFly.getInstance().getDataBridge().getOrDefault(DataValue.PLAYER_TRAIL, null, new String[]{u.toString()});
		if (V.debug) {
			Console.debug("");
			Console.debug("------Loading particle trail------");
			Console.debug("Player: " + u.toString());
			Console.debug("Value from data: " + String.valueOf(particle));
			Console.debug("Default trail enabled: " + V.particleDefault);
			Console.debug("Default trail is: " + V.particleType);
			Console.debug("Returning trail: " +  (particle != null ? particle: (V.particleDefault ? V.particleType : "")));
			Console.debug("------End particle trail------");
			Console.debug("");
		}
		return particle != null ? particle: (V.particleDefault ? V.particleType : "");
	}
	
	/**
	 * Set a players particle trail.
	 * If particle is set to null or if the trail specified does not exist TempFly will attempt to use the default trail if enabled in the config.
	 * If it is set to an empty string however the particle will be disabled, IE no trail. This is what the remove trail command does.
	 * @param u the player
	 * @param particle the particle
	 */
	public static void setTrail(UUID u, String particle) {
		TempFly.getInstance().getDataBridge().stageChange(DataValue.PLAYER_TRAIL, particle, new String[]{u.toString()});
		Flyer f = FlyHandle.getFlyer(Bukkit.getPlayer(u));
		if (f != null) {
			f.setTrail(particle);
		}
	}
	
}
