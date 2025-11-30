package net.awardedbadge813.beaconite813.entity;

import net.awardedbadge813.beaconite813.Config;
import net.awardedbadge813.beaconite813.entity.custom.BeaconBeamHolder;
import net.awardedbadge813.beaconite813.entity.custom.CanFormBeacon;
import net.awardedbadge813.beaconite813.item.ModItems;
import net.awardedbadge813.beaconite813.screen.custom.UnstableBeaconMenu;
import net.minecraft.core.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.*;
import static java.lang.Thread.sleep;
import static net.minecraft.world.item.Items.NETHER_STAR;
import static net.minecraft.world.level.block.Blocks.*;

public class UnstableBeaconBlockEntity extends BeaconBeamHolder implements MenuProvider, CanFormBeacon {

    public UnstableBeaconBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntities.UNSTABLE_BEACON_BE.get(),
                pos,
                blockState);
        data = new ContainerData() {
            @Override
            public int get(int i) {
                switch (i) {
                    case 0 -> {
                        return timeToExplode;
                    }
                    case 1 -> {
                        return beaconRings;
                    }
                    case 2 -> {
                        return beaconActive;
                    }
                    case 3 -> {
                        return explosionActive;
                    }
                    default -> {
                        return 0;
                    }
                }
            }

            @Override
            public void set(int i, int i1) {
                switch (i) {
                    case 0 -> {
                        timeToExplode=i1;
                        setChanged();
                        updateLevel(getBlockPos());
                    }
                    case 1 -> {
                        beaconRings=i1;
                        setChanged();
                        updateLevel(getBlockPos());
                    }
                    case 2 -> {
                        beaconActive=i1;
                        setChanged();
                        updateLevel(getBlockPos());
                    }
                    case 3 -> {
                        explosionActive=i1;
                        setChanged();
                        updateLevel(getBlockPos());
                    }
                    default -> {
                    }
                }

            }

            @Override
            public int getCount() {
                return 4;
            }

        };
    }



    public final ItemStackHandler itemHandler = new ItemStackHandler(1) {
        @Override
        protected int getStackLimit(int slot, @NotNull ItemStack stack) {
            return 1;
        }

        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
            assert level != null;
            if(!level.isClientSide()) {
                level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
            }
        }
    };
    public void drops() {
        SimpleContainer inventory = new SimpleContainer(itemHandler.getSlots());
        for (int i=0; i<itemHandler.getSlots(); i++) {
            inventory.setItem(i, itemHandler.getStackInSlot(i));
        }

        assert level != null;
        Containers.dropContents(level, this.worldPosition, inventory);
        this.remove();
    }

    @Override
    public @NotNull Component getDisplayName() {
        return Component.literal("block.beaconite813.unstable_beacon_be");
    }

    @Override
    public @Nullable AbstractContainerMenu createMenu(int i, @NotNull Inventory inventory, @NotNull Player player) {
        return new UnstableBeaconMenu(i, inventory, this, this.data);
    }


    @Override
    public @NotNull CompoundTag getUpdateTag(HolderLookup.@NotNull Provider pRegistries) {
        return saveWithoutMetadata(pRegistries);
    }


    public void tick(Level level, BlockPos pos) {
        int i = pos.getX();
        int j = pos.getY();
        int k = pos.getZ();
        beaconRings = this.updateLevel(new BlockPos (i, j, k));
        this.level=level;
        this.beaconActive = getSkyStatus(level, pos)==1 && beaconRings>4 ? 1:0;
        if (itemHandler.getStackInSlot(0).getItem() == NETHER_STAR.asItem() && this.explosionActive==0 && this.beaconActive==1){
            this.explosionActive=1;
            itemHandler.extractItem(0, 1, false);
        }
        if(this.explosionActive==1 && this.timeToExplode>0 && this.beaconActive==1) {
            this.timeToExplode--;
            if(timeToExplode%10==0 && timeToExplode>120) {
                level.playSound(null, pos.getX(), pos.getY(), pos.getZ(), SoundEvents.BEACON_DEACTIVATE, SoundSource.BLOCKS, 5.1f-5*((float) (timeToExplode/explosionMaxTime)),  (0f+(float) (timeToExplode)/200));

            } else if (timeToExplode%10==0) {
                level.playSound(null, pos.getX(), pos.getY(), pos.getZ(), SoundEvents.GENERIC_EXPLODE, SoundSource.BLOCKS, 4f,  0f);
            }
        } else  if (beaconActive==0){
            this.timeToExplode = explosionMaxTime;
            this.explosionActive=0;
        }
        if(this.timeToExplode<=0) {
            float radius = (float) min(Config.EXPLOSION_RADIUS.getAsInt()*(min(beaconRings-4, 7)), Config.MAX_EXPLODE_RADIUS.getAsInt()) * 0.75f;

            level.explode(null, pos.getX(), pos.getY(), pos.getZ(), radius, Level.ExplosionInteraction.NONE);

            beaconBomb(pos, min(Config.EXPLOSION_RADIUS.getAsInt()*(max((beaconRings-4), 1)), Config.MAX_EXPLODE_RADIUS.getAsInt()), 20);

            ArrayList<ItemStack> roll = getLootRoll();
            SimpleContainer inventory = new SimpleContainer(roll.size());
            for(int index = 0; index <roll.size(); index++) {
                inventory.setItem(index, roll.get(index));
            }
            Containers.dropContents(level, this.worldPosition, inventory);


            this.explosionActive=0;
        }
    }

    public ArrayList<ItemStack> getLootRoll() {
        int random = (int) (random()*50)*(beaconRings-4);
        ArrayList<ItemStack> roll = new ArrayList<>();
        roll.add(new ItemStack(ModItems.PURE_BEACONITE.get().asItem(), 16));
        if(random>49) {
            roll.add(new ItemStack(ModItems.PURE_BEACONITE.get().asItem(), 16));
            roll.add(new ItemStack(ModItems.CATALYST.get().asItem(), 1));
        }
        return roll;
    }

    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    public void remove() {
        assert level != null;
        level.removeBlockEntity(getBlockPos());
    }





    private int getX(BlockPos pos, int n) {
        return pos.getX()-n;
    }
    private int getZ(BlockPos pos, int n) {
        return pos.getZ()-n;
    }

    private int levelSize(int n) {
        return 1+2*n;
    }

    private boolean checkBlockStateForBeaconBlock(BlockPos pPos) {
        assert level != null;
        return level.getBlockState(pPos).is(BlockTags.BEACON_BASE_BLOCKS);
    }
    private int updateLevel(BlockPos pos) {
        int i;
        int y=pos.getY()-1;
        for (i = 1; i<=MAX_LEVELS; i++){
            for (int x = getX(pos, i); x < getX(pos, i) + levelSize(i); x++) {
                for (int z = getZ(pos, i); z < (getZ(pos, i) + levelSize(i)); z++) {
                    if (!checkBlockStateForBeaconBlock(new BlockPos(x,y,z))){
                        return i-1;
                    }
                }
            }
            y--;
        }
        return i - 1;
    }

    public float getDist(BlockPos pos1, BlockPos pos2) {
        int x1=pos1.getX();
        int y1=pos1.getY();
        int z1=pos1.getZ();
        int x2=pos2.getX();
        int y2=pos2.getY();
        int z2=pos2.getZ();
        return (float) sqrt( pow((float)(x1-x2), 2) + pow((float) (y1-y2),2) + pow((float) (z1-z2), 2));
    }

    public void beaconBomb(BlockPos pos, int radius, int noiseFactor) {
        int i=pos.getX();
        int j=pos.getY();
        int k=pos.getZ();
        AABB damageRange = new AABB(pos).inflate(radius*1.1f);
        assert level != null;
        List<LivingEntity> list = level.getEntitiesOfClass(LivingEntity.class, damageRange);
        for(LivingEntity entity: list) {
            float distance = getDist(pos, entity.getOnPos());
            if(entity instanceof WitherBoss)
                entity.hurt(new DamageSource(this.level.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(DamageTypes.GENERIC_KILL), entity, null, null), 10000);
            else {
                entity.hurt(new DamageSource(this.level.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(DamageTypes.EXPLOSION), entity, null, null), (float) pow(distance-radius, 2));

            }
            }

        for (int x=i-radius; x<=i+radius; x++) {
            try {
                sleep(50);
            } catch (Exception ignored) {}
            for (int y=j-radius; y<=j+radius; y++) {
                for (int z=k-radius; z<=k+radius; z++) {
                    BlockPos pPos=new BlockPos(x, y, z);
                    float drawnLine= getDist(pos, pPos);
                    boolean removing=radius-drawnLine>=5;


                    boolean noiseMarked= random() > (double) 250/(noiseFactor*radius);
                    if((removing || ((radius-drawnLine)>=0 && noiseMarked))){
                        tryBlowUpBlock(pPos);
                    }
                }
            }

        }
        level.playSound(null, pos.getX(), pos.getY(), pos.getZ(), SoundEvents.GENERIC_EXPLODE, SoundSource.BLOCKS, 3f, -10f);


    }





    @Override
    protected void saveAdditional(CompoundTag pTag, HolderLookup.@NotNull Provider pRegistries) {
        pTag.put("inventory", itemHandler.serializeNBT(pRegistries));
        pTag.putInt("explode_countdown", timeToExplode);
        pTag.putInt("active_rings", beaconRings);
        pTag.putInt("beam_active", beaconActive);
        pTag.putInt("tnt_active", explosionActive);

        super.saveAdditional(pTag, pRegistries);
    }
    @Override
    protected void loadAdditional(@NotNull CompoundTag pTag, HolderLookup.@NotNull Provider pRegistries) {
        super.loadAdditional(pTag, pRegistries);
        itemHandler.deserializeNBT(pRegistries, pTag.getCompound("inventory"));
        timeToExplode = pTag.getInt("explode_countdown");
        beaconRings = pTag.getInt("active_rings");
        beaconActive = pTag.getInt("beam_active");
        explosionActive = pTag.getInt("tnt_active");


    }
    public void tryBlowUpBlock(BlockPos pos) {
        assert level != null;
        BlockState blockstate = level.getBlockState(pos);
        if(!blockstate.is(BlockTags.WITHER_IMMUNE) && Config.BOMBS_DESTROY_BLOCKS.getAsBoolean()) {
            level.setBlockAndUpdate(pos, AIR.defaultBlockState());
        }
    }


    @Override
    public boolean IsBeaconActive() {
        return true;
    }

    @Override
    public List<BeaconBeamHolder.BeaconBeamSection> getBeamSections() {
        BeaconBeamHolder.BeaconBeamSection beamSection = new BeaconBeamHolder.BeaconBeamSection();
        assert level != null;
        beamSection.setParams(11546150, level.getMaxBuildHeight() - getBlockPos().getY());
        this.beamSections=List.of(beamSection);
        return List.of(beamSection);
    }




//Config.MAXLEVELUNSTABLEBEACON.getAsInt();
    private static final int MAX_LEVELS = Config.MAX_LEVEL_UNSTABLE_BEACON.getAsInt();
    protected final ContainerData data;
    private int timeToExplode = Config.TIME_EXPLODE.getAsInt();
    private int beaconActive = 0;
    private int beaconRings=0;
    private int explosionActive= 0;
    private final int explosionMaxTime = Config.TIME_EXPLODE.getAsInt();

}
