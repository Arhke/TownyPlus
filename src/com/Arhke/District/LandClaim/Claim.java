package com.Arhke.District.LandClaim;

import com.Arhke.District.FileIO.DataManager;
import com.Arhke.District.Main;
import com.Arhke.District.Utils.Base;
import com.Arhke.District.Utils.Direction;
import com.Arhke.District.Utils.Vector2D;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class Claim extends Base {

	private Vector2D _v1; //Left Top Corner
	private Vector2D _v2; //Right Bottom Corner

	public static final String V1Key = "v1", V2Key = "v2";
	public Claim(Vector2D v1, Vector2D v2) {
		_v1 = new Vector2D((int)Math.min(v1.getX(), v2.getX()), (int)Math.min(v1.getZ(), v2.getZ()));
		_v2 = new Vector2D((int)Math.max(v1.getX(), v2.getX()), (int)Math.max(v1.getZ(), v2.getZ()));

	}
	public Claim(DataManager dm){
		Vector2D v1 = new Vector2D(dm.getDataManager(V1Key));
		Vector2D v2 = new Vector2D(dm.getDataManager(V2Key));
		_v1 = new Vector2D((int)Math.min(v1.getX(), v2.getX()), (int)Math.min(v1.getZ(), v2.getZ()));
		_v2 = new Vector2D((int)Math.max(v1.getX(), v2.getX()), (int)Math.max(v1.getZ(), v2.getZ()));
	}

	public Vector2D getV1() {
		return _v1;
	}
	public Vector2D getV2() {
		return _v2;
	}
	public boolean isInside(Location loc) {
		return _v1.getX() <= loc.getX() && _v1.getZ() <= loc.getZ() &&
				_v2.getX() >= loc.getX() && _v2.getZ() >= loc.getZ();
	}
	public boolean overlaps(Claim claim){
		if (_v1.getX() > claim.getV2().getX() || _v1.getZ() > claim.getV2().getZ() ||
		_v2.getX() < claim.getV1().getX() || _v2.getZ() < claim.getV1().getZ()){
			return false;
		}
		return true;
	}
	public void expand(Direction direction, int amount){
		if (direction.getX() + direction.getZ() <= -1){
			_v1 = new Vector2D((int)_v1.getX() + amount * direction.getX(), (int)_v1.getZ() + amount * direction.getZ());
		}else {
			_v2 = new Vector2D((int)_v2.getX() + amount * direction.getX(), (int)_v2.getZ() + amount * direction.getZ());
		}
	}
	public void showClaim(Main Instance, Player player){
		new BukkitRunnable(){
			int i = 0;
			Player _player = player;
			@Override
			public void run() {
				if (i<40){
					for(double j = _v1.getX(); j < _v2.getX(); j+=.5){
						_player.spawnParticle(Instance.getValues().ClaimParticle, j, _player.getEyeLocation().getY(),
								_v1.getZ(), 1, 0, 0, 0, new Particle.DustOptions(Color.AQUA, 1));
						_player.spawnParticle(Instance.getValues().ClaimParticle, j, _player.getEyeLocation().getY(),
								_v2.getZ(), 1, 0, 0, 0, new Particle.DustOptions(Color.AQUA, 1));
					}
					for(double j = _v1.getZ(); j < _v2.getZ(); j+=.5) {
						_player.spawnParticle(Instance.getValues().ClaimParticle, _v1.getX(), _player.getEyeLocation().getY(),
								j, 1, 0, 0, 0, new Particle.DustOptions(Color.AQUA, 1));
						_player.spawnParticle(Instance.getValues().ClaimParticle, _v2.getX(), _player.getEyeLocation().getY(),
								j, 1, 0, 0, 0, new Particle.DustOptions(Color.AQUA, 1));
					}
					i++;
				}else {
					this.cancel();
				}
			}
		}.runTaskTimer(Instance, 10L, 5L);
	}
	public int claimsNeeded(Direction direction, int amount) {
		return (int)(amount * Math.abs(direction.getX()) * (_v2.getZ() - _v1.getZ()) + amount * Math.abs(direction.getZ()) * (_v2.getX() - _v1.getX())
				+ amount * amount * direction.getX() * direction.getZ());
	}
	public int getClaimBlocks() {
		return (int)((_v2.getX()-_v1.getX())*(_v2.getZ()-_v1.getZ()));
	}
	public void write(DataManager dm) {
		_v1.write(dm.getDataManager(V1Key));
		_v2.write(dm.getDataManager(V2Key));
	}

}
