package net.awardedbadge813.beaconite813.entity;

import net.awardedbadge813.beaconite813.Config;
import net.awardedbadge813.beaconite813.entity.custom.ExplosionEntity;
import net.awardedbadge813.beaconite813.item.ModItems;
import net.awardedbadge813.beaconite813.screen.custom.UnstableBeaconMenu;
import net.minecraft.client.resources.sounds.Sound;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jetbrains.annotations.Nullable;

import static java.lang.Math.*;
import static java.lang.Thread.sleep;
import static net.minecraft.world.item.Items.NETHER_STAR;
import static net.minecraft.world.level.block.Blocks.*;

public class UnstableBeaconBlockEntity extends BlockEntity implements MenuProvider {

    public static final ResourceLocation BEAM_LOCATION = ResourceLocation.withDefaultNamespace("textures/entity/beacon_beam.png");

    private static final Log log = LogFactory.getLog(UnstableBeaconBlockEntity.class);

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
                    }
                    case 1 -> {
                        beaconRings=i1;
                    }
                    case 2 -> {
                        beaconActive=i1;
                    }
                    case 3 -> {
                        explosionActive=i1;
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
        protected int getStackLimit(int slot, ItemStack stack) {
            return 1;
        }

        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
            if(!level.isClientSide()) {
                level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
            };
        }
    };
    public void drops() {
        SimpleContainer inventory = new SimpleContainer(itemHandler.getSlots());
        for (int i=0; i<itemHandler.getSlots(); i++) {
            inventory.setItem(i, itemHandler.getStackInSlot(i));
        }

        Containers.dropContents(this.level, this.worldPosition, inventory);
        this.remove(getBlockPos());
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.beaconite813.unstable_beacon_be");
    }

    @Override
    public @Nullable AbstractContainerMenu createMenu(int i, Inventory inventory, Player player) {
        return new UnstableBeaconMenu(i, inventory, this, this.data);
    }


    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider pRegistries) {
        return saveWithoutMetadata(pRegistries);
    }


    public void tick(Level level, BlockPos pos, BlockState state) {
        int i = pos.getX();
        int j = pos.getY();
        int k = pos.getZ();
        boolean foundBadBlock=false;
        beaconRings = this.updateLevel(new BlockPos (i, j, k));
        if (this.beaconRings >= MAX_LEVELS) {
            for (int heightclear = 1; j+heightclear < level.getMaxBuildHeight(); heightclear++) {
                BlockPos pPos = new BlockPos(i, (j+heightclear), k);
                if(!checkBlockStateForBeaconPassable(pPos)) {
                    foundBadBlock=true;
                }
            }
            if (!foundBadBlock||!Config.UNSTABLEBEACOONSEESSKY.getAsBoolean()) {
                this.beaconActive = 1;
            } else {
                this.beaconActive = 0;
            }

        }
        if (itemHandler.getStackInSlot(0).getItem() == NETHER_STAR.asItem() && this.explosionActive==0&& this.beaconActive==1){
            this.explosionActive=1;
            itemHandler.extractItem(0, 1, false);
        }
        if(this.explosionActive==1&& this.timeToExplode>0) {
            this.timeToExplode--;
            if(timeToExplode%3==0 && timeToExplode>120) {
                level.playSound(null, pos.getX(), pos.getY(), pos.getZ(), SoundEvents.BEACON_DEACTIVATE, SoundSource.BLOCKS, 5.1f-5*((float) (timeToExplode/explosionMaxTime)),  (0f+(float) (timeToExplode)/200));

            } else if (timeToExplode%3==0) {
                level.playSound(null, pos.getX(), pos.getY(), pos.getZ(), SoundEvents.GENERIC_EXPLODE, SoundSource.BLOCKS, 4f,  0f);
            }
        }
        if(this.timeToExplode<=0) {
            float radius = (float) Config.EXPLOSION_RADIUS.getAsInt() * 0.75f;
            level.explode(null, pos.getX(), pos.getY(), pos.getZ(), radius, Level.ExplosionInteraction.NONE);
            beaconBomb(pos, 25, 20);
            SimpleContainer inventory = new SimpleContainer(1);
            inventory.setItem(0, new ItemStack(ModItems.PUREBEACONITE.get(), 16));
            Containers.dropContents(this.level, this.worldPosition, inventory);
            //Config.EXPLOSION_RADIUS.getAsInt()



            this.explosionActive=0;
        }


    }
    public void remove(BlockPos lPos) {
        level.removeBlockEntity(getBlockPos());
    }
    public void testBlock(BlockPos mPos) {
        assert level != null;
        level.setBlockAndUpdate(mPos, Blocks.DIRT.defaultBlockState());

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
        return level.getBlockState(pPos).is(BlockTags.BEACON_BASE_BLOCKS);
    }
    private boolean checkBlockStateForBeaconPassable(BlockPos pos) {
        return level.getBlockState(pos).is(AIR.defaultBlockState().getBlock())||level.getBlockState(pos).is(BEDROCK.defaultBlockState().getBlock());
    }
    private int updateLevel(BlockPos pos) {
        int i=1;
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

        for (int x=i-radius; x<=i+radius; x++) {
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
            level.playSound((Player)null, pos.getX(), pos.getY(), pos.getZ(), SoundEvents.GENERIC_EXPLODE, SoundSource.BLOCKS, 3f, -10f);

        }

    }





    @Override
    protected void saveAdditional(CompoundTag pTag, HolderLookup.Provider pRegistries) {
        pTag.put("inventory", itemHandler.serializeNBT(pRegistries));
        pTag.putInt("explode_countdown", timeToExplode);
        pTag.putInt("active_rings", beaconRings);
        pTag.putInt("beam_active", beaconActive);
        pTag.putInt("tnt_active", explosionActive);

        super.saveAdditional(pTag, pRegistries);
    }
    @Override
    protected void loadAdditional(CompoundTag pTag, HolderLookup.Provider pRegistries) {
        super.loadAdditional(pTag, pRegistries);
        itemHandler.deserializeNBT(pRegistries, pTag.getCompound("inventory"));
        timeToExplode = pTag.getInt("explode_countdown");
        beaconRings = pTag.getInt("active_rings");
        beaconActive = pTag.getInt("beam_active");
        explosionActive = pTag.getInt("tnt_active");


    }
    public void tryBlowUpBlock(BlockPos pos) {
        BlockState blockstate = level.getBlockState(pos);
        if(!blockstate.is(BlockTags.WITHER_IMMUNE) && Config.BOMBSDESTROYBLOCKS.getAsBoolean() && !(blockstate.getBlock()==WATER) && !(blockstate.getBlock()==LAVA)) {
            level.setBlockAndUpdate(pos, AIR.defaultBlockState());
        }
    }




//Config.MAXLEVELUNSTABLEBEACON.getAsInt();
    private static final int MAX_LEVELS = Config.MAXLEVELUNSTABLEBEACON.getAsInt();
    protected final ContainerData data;
    private int timeToExplode = Config.TIME_EXPLODE.getAsInt()-400;
    private int beaconActive = 0;
    private int beaconRings=0;
    private int explosionActive= 0;
    private int explosionMaxTime = Config.TIME_EXPLODE.getAsInt();

}
