package com.Arhke.District.LandClaim;

import com.Arhke.District.Main;
import com.Arhke.District.Utils.MainBase;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ClaimManager extends MainBase {
    private Set<Claim> _claims = new HashSet<Claim>();
    public ClaimManager(Main Instance){
        super(Instance);
    }
    public boolean claimOverlaps(Claim claim) {
        for (Claim c : _claims) {
            if (c.overlaps(claim)) {
                return true;
            }

        }
        return false;
    }
    public void registerClaim(Claim c){
        _claims.add(c);
    }
    public void unregisterClaim(Claim c){
        _claims.remove(c);
    }
    public boolean isClaimed(Location loc){
        return _claims.stream().anyMatch(s -> s.isInside(loc));
    }


}
