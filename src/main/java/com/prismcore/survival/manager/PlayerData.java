package com.prismcore.survival.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class PlayerData {
   private final UUID uuid;
   private double shards;
   private double money;
   private double shopSpent;
   private final Map<String, Integer> keys = new HashMap();
   private final List<String> offlinePayments = new ArrayList();
   private String name;
   private long shardBoosterExpiry;
   private long rtpCooldown;
   private String pendingRtpType;
   private boolean nameHidden = false;
   private boolean disguised = false;
   private String disguiseName = null;
   private String disguiseSkinTexture = null;
   private String disguiseSkinSignature = null;
   private String originalPrimaryGroup = null;
   private List<String> originalGroups = null;
   private String originalPrefix = null;
   private long lastSeenUpdate = 0L;
   private String pendingRtpTargetServer;
   private String pendingSpawnName;
   private String pendingSpawnWorld;
   private Double pendingSpawnX;
   private Double pendingSpawnY;
   private Double pendingSpawnZ;
   private Float pendingSpawnYaw;
   private Float pendingSpawnPitch;

   public PlayerData(UUID var1) {
      this.uuid = var1;
      this.shards = (double)0.0F;
      this.money = (double)0.0F;
      this.shopSpent = (double)0.0F;
      this.shardBoosterExpiry = 0L;
      this.rtpCooldown = 0L;
      this.name = null;
      this.pendingRtpType = null;
   }

   public String getName() {
      return this.name;
   }

   public void setName(String var1) {
      this.name = var1;
   }

   public UUID getUuid() {
      return this.uuid;
   }

   public double getShards() {
      return this.shards;
   }

   public void setShards(double var1) {
      this.shards = var1;
   }

   public void addShards(double var1) {
      this.shards += var1;
   }

   public void removeShards(double var1) {
      this.shards -= var1;
   }

   public double getMoney() {
      return this.money;
   }

   public void setMoney(double var1) {
      this.money = var1;
   }

   public void addMoney(double var1) {
      this.money += var1;
   }

   public void removeMoney(double var1) {
      this.money -= var1;
   }

   public double getShopSpent() {
      return this.shopSpent;
   }

   public void setShopSpent(double var1) {
      this.shopSpent = var1;
   }

   public void addShopSpent(double var1) {
      this.shopSpent += var1;
   }

   public void addKey(String var1) {
      int var2 = (Integer)this.keys.getOrDefault(var1, 0);
      this.keys.put(var1, var2 + 1);
   }

   public void removeKey(String var1) {
      int var2 = (Integer)this.keys.getOrDefault(var1, 0);
      if (var2 > 0) {
         this.keys.put(var1, var2 - 1);
      }

   }

   public int getKeyCount(String var1) {
      return (Integer)this.keys.getOrDefault(var1, 0);
   }

   public void setKeyCount(String var1, int var2) {
      this.keys.put(var1, var2);
   }

   public Map<String, Integer> getKeys() {
      return new HashMap(this.keys);
   }

   public Map<String, Integer> getAllKeys() {
      return this.keys;
   }

   public long getLastSeenUpdate() {
      return this.lastSeenUpdate;
   }

   public void setLastSeenUpdate(long var1) {
      this.lastSeenUpdate = var1;
   }

   public boolean hasActiveShardBooster() {
      return this.shardBoosterExpiry > System.currentTimeMillis();
   }

   public long getShardBoosterExpiry() {
      return this.shardBoosterExpiry;
   }

   public void setShardBoosterExpiry(long var1) {
      this.shardBoosterExpiry = var1;
   }

   public long getShardBoosterRemainingSeconds() {
      return !this.hasActiveShardBooster() ? 0L : (this.shardBoosterExpiry - System.currentTimeMillis()) / 1000L;
   }

   public long getRtpCooldown() {
      return this.rtpCooldown;
   }

   public void setRtpCooldown(long var1) {
      this.rtpCooldown = var1;
   }

   public String getPendingRtpType() {
      return this.pendingRtpType;
   }

   public void setPendingRtpType(String var1) {
      this.pendingRtpType = var1;
   }

   public String getPendingRtpTargetServer() {
      return this.pendingRtpTargetServer;
   }

   public void setPendingRtpTargetServer(String var1) {
      this.pendingRtpTargetServer = var1;
   }

   public String getPendingSpawnName() {
      return this.pendingSpawnName;
   }

   public void setPendingSpawnName(String var1) {
      this.pendingSpawnName = var1;
   }

   public String getPendingSpawnWorld() {
      return this.pendingSpawnWorld;
   }

   public void setPendingSpawnWorld(String var1) {
      this.pendingSpawnWorld = var1;
   }

   public Double getPendingSpawnX() {
      return this.pendingSpawnX;
   }

   public void setPendingSpawnX(Double var1) {
      this.pendingSpawnX = var1;
   }

   public Double getPendingSpawnY() {
      return this.pendingSpawnY;
   }

   public void setPendingSpawnY(Double var1) {
      this.pendingSpawnY = var1;
   }

   public Double getPendingSpawnZ() {
      return this.pendingSpawnZ;
   }

   public void setPendingSpawnZ(Double var1) {
      this.pendingSpawnZ = var1;
   }

   public Float getPendingSpawnYaw() {
      return this.pendingSpawnYaw;
   }

   public void setPendingSpawnYaw(Float var1) {
      this.pendingSpawnYaw = var1;
   }

   public Float getPendingSpawnPitch() {
      return this.pendingSpawnPitch;
   }

   public void setPendingSpawnPitch(Float var1) {
      this.pendingSpawnPitch = var1;
   }

   public List<String> getOfflinePayments() {
      return this.offlinePayments;
   }

   public void addOfflinePayment(String var1) {
      this.offlinePayments.add(var1);
   }

   public void clearOfflinePayments() {
      this.offlinePayments.clear();
   }

   public boolean isNameHidden() {
      return this.nameHidden;
   }

   public void setNameHidden(boolean var1) {
      this.nameHidden = var1;
   }

   public boolean isDisguised() {
      return this.disguised;
   }

   public void setDisguised(boolean var1) {
      this.disguised = var1;
   }

   public String getDisguiseName() {
      return this.disguiseName;
   }

   public void setDisguiseName(String var1) {
      this.disguiseName = var1;
   }

   public String getDisguiseSkinTexture() {
      return this.disguiseSkinTexture;
   }

   public void setDisguiseSkinTexture(String var1) {
      this.disguiseSkinTexture = var1;
   }

   public String getDisguiseSkinSignature() {
      return this.disguiseSkinSignature;
   }

   public void setDisguiseSkinSignature(String var1) {
      this.disguiseSkinSignature = var1;
   }

   public String getOriginalPrimaryGroup() {
      return this.originalPrimaryGroup;
   }

   public void setOriginalPrimaryGroup(String var1) {
      this.originalPrimaryGroup = var1;
   }

   public List<String> getOriginalGroups() {
      return this.originalGroups;
   }

   public void setOriginalGroups(List<String> var1) {
      this.originalGroups = var1;
   }

   public String getOriginalPrefix() {
      return this.originalPrefix;
   }

   public void setOriginalPrefix(String var1) {
      this.originalPrefix = var1;
   }
}
