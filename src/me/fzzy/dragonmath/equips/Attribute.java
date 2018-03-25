package me.fzzy.dragonmath.equips;

import me.fzzy.dragonmath.util.Distance;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;

public class Attribute {

    private boolean heal;
    private int healAmt;
    private Target healTarget;

    private boolean dmg;
    private int dmgAmt;
    private Target dmgTarget;

    private boolean potion;
    private PotionEffect effect;
    private Target effectTarget;

    private Particle particle;
    private int particleAmt;

    public void trigger(Player from, ArrayList<LivingEntity> targets) {
        for (LivingEntity le : targets) {

            //Heal
            if (heal) {
                if (!(healTarget == Target.ENEMY && le instanceof Player)) {
                    if (!(healTarget == Target.FRIENDLY && !(le instanceof Player))) {
                        if (!(healTarget == Target.SELF && !le.getUniqueId().equals(from.getUniqueId()))) {
                            le.setHealth(Math.min(20, le.getHealth() + healAmt));
                            le.getWorld().spawnParticle(particle, Distance.getCenterLocation(le), particleAmt, 0.5, 0.5, 0.5);
                        }
                    }
                }
            }

            //Damage
            if (dmg) {
                if (!(dmgTarget == Target.ENEMY && le instanceof Player)) {
                    if (!(dmgTarget == Target.FRIENDLY && !(le instanceof Player))) {
                        if (!(dmgTarget == Target.SELF && !le.getUniqueId().equals(from.getUniqueId()))) {
                            le.damage(dmgAmt, from);
                            le.getWorld().spawnParticle(particle, Distance.getCenterLocation(le), particleAmt, 0.5, 0.5, 0.5);
                        }
                    }
                }
            }

            //Effect
            if (potion) {
                if (!(effectTarget == Target.ENEMY && le instanceof Player)) {
                    if (!(effectTarget == Target.FRIENDLY && !(le instanceof Player))) {
                        if (!(effectTarget == Target.SELF && !le.getUniqueId().equals(from.getUniqueId()))) {
                            le.addPotionEffect(effect);
                            le.getWorld().spawnParticle(particle, Distance.getCenterLocation(le), particleAmt, 0.5, 0.5, 0.5);
                        }
                    }
                }
            }
        }
    }

    public boolean hasHeal() {
        return heal;
    }

    public int getHealAmt() {
        return healAmt;
    }

    public Target getHealTarget() {
        return healTarget;
    }

    public boolean hasDamage() {
        return dmg;
    }

    public int getDamageAmt() {
        return dmgAmt;
    }

    public Target getDamageTarget() {
        return dmgTarget;
    }

    public boolean hasEffect() {
        return potion;
    }

    public PotionEffect getEffect() {
        return effect;
    }

    public Target getEffectTarget() {
        return effectTarget;
    }

    public String serialize() {
        return heal
                + ";;" + (!heal ? "" : healAmt)
                + ";;" + (!heal ? "" : healTarget.name())
                + ";;" + dmg
                + ";;" + (!dmg ? "" : dmgAmt)
                + ";;" + (!dmg ? "" : dmgTarget.name())
                + ";;" + potion
                + ";;" + (!potion ? "" : effect.getType().getName())
                + ";;" + (!potion ? "" : effect.getAmplifier())
                + ";;" + (!potion ? "" : effect.getDuration())
                + ";;" + (!potion ? "" : effectTarget.name())
                + ";;" + particle.name()
                + ";;" + particleAmt;
    }

    public static Attribute getFromSerial(String serial) {
        String[] data = serial.split(";;");
        Builder builder = Attribute.builder();

        //Heal
        if (Boolean.parseBoolean(data[0]))
            builder.heal(Integer.parseInt(data[1]), Target.valueOf(data[2]));
        //Damage
        if (Boolean.parseBoolean(data[3]))
            builder.damage(Integer.parseInt(data[4]), Target.valueOf(data[5]));
        //Potion
        if (Boolean.parseBoolean(data[6])) {
            PotionEffectType type = PotionEffectType.getByName(data[7]);
            int amp = Integer.parseInt(data[8]);
            int duration = Integer.parseInt(data[9]);
            Target target = Target.valueOf(data[10]);
            builder.effect(new PotionEffect(type, duration, amp), target);
        }
        builder.particle(Particle.valueOf(data[11]), Integer.parseInt(data[12]));

        return builder.build();
    }

    Attribute(boolean heal, int healAmt, Target healTarget, boolean dmg, int dmgAmt, Target dmgTarget, boolean potion, PotionEffect effect, Target effectTarget, Particle particle, int particleAmt) {
        this.heal = heal;
        this.healAmt = healAmt;
        this.healTarget = healTarget;
        this.dmg = dmg;
        this.dmgAmt = dmgAmt;
        this.dmgTarget = dmgTarget;
        this.potion = potion;
        this.effect = effect;
        this.effectTarget = effectTarget;
        this.particle = particle;
        this.particleAmt = particleAmt;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {

        boolean heal;
        int healAmt;
        Target healTarget;

        boolean dmg;
        int dmgAmt;
        Target dmgTarget;

        boolean potion;
        PotionEffect effect;
        Target effectTarget;

        Particle particle;
        int particleAmt;

        public Builder() {
            heal = false;
            dmg = false;
            potion = false;
            particle = Particle.FIREWORKS_SPARK;
            particleAmt = 0;
        }

        public Builder particle(Particle particle, int amt) {
            this.particle = particle;
            this.particleAmt = amt;
            return this;
        }

        public Builder heal(int amt, Target target) {
            heal = true;
            healAmt = amt;
            healTarget = target;
            return this;
        }

        public Builder damage(int amt, Target target) {
            dmg = true;
            dmgAmt = amt;
            dmgTarget = target;
            return this;
        }

        public Builder effect(PotionEffect effect, Target target) {
            potion = true;
            this.effect = effect;
            effectTarget = target;
            return this;
        }

        public Attribute build() {
            return new Attribute(heal, healAmt, healTarget, dmg, dmgAmt, dmgTarget, potion, effect, effectTarget, particle, particleAmt);
        }

    }

}
