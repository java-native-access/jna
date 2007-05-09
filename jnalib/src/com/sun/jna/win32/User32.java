package com.sun.jna.win32;

import com.sun.jna.Structure;
import com.sun.jna.annotation.*;

/**
 *
 * @author  Todd Fast, todd.fast@sun.com
 */
@NativeLibrary(name="user32", convention=CallingConvention.STDCALL)
public interface User32
{
	@NativeFunction(name="FindWindowA")
	public int findWindow(String winClass, String title);

	// T0DD: Don't forget to protect against passing arbitrary types to a function!
	@NativeFunction(name="FlashWindowEx")
//	public boolean flashWindowEx(Structure<FlashWinInfo> info);
	public boolean flashWindowEx(FlashWinInfo info);

	@NativeFunction(name="LoadIconA")
	public int loadIcon(int hInstance, int name); //NativePointer iconName);

	@NativeFunction(name="LoadImageA")
	public int loadImage( 
	  int hinst,   // handle to instance 
	  String name,  // image to load 
	  int type,        // image type 
	  int xDesired,     // desired width 
	  int yDesired,     // desired height 
	  int load        // load options 
	);

	@NativeFunction(name="DestroyIcon")
	public boolean destroyIcon(int hicon);


	@NativeStructure
	public static class FlashWinInfo extends Structure
	{
		@NativeField(size=4)
		public int size;

		@NativeField(size=4)
		public int hwnd;

		@NativeField(size=4)
		public int flags;

		@NativeField(size=4)
		public int count;

		@NativeField(size=4)
		public int timeout;

		public static final int STOP=0;
		public static final int CAPTION=1;
		public static final int TRAY=2; 
		public static final int ALL=3;
		public static final int TIMER=4; 
		public static final int TIMERNOFG=12; 
	}


//	@NativeStructure
//	public static interface FlashWinInfo_test //extends Structure
//	{
//		@NativeField(size=4)
//		public int size(int value);
//
//		@NativeField(size=4)
//		public int hwnd(int value);
//
//		@NativeField(size=4)
//		public int flags(int value);
//
//		@NativeField(size=4)
//		public int count(int value);
//
//		@NativeField(size=4)
//		public int timeout(int value);
//
//		public static final int STOP=0;
//		public static final int CAPTION=1;
//		public static final int TRAY=2; 
//		public static final int ALL=3;
//		public static final int TIMER=4; 
//		public static final int TIMERNOFG=12; 
//	}


	public static final int IMAGE_BITMAP=0;
	public static final int IMAGE_ICON=1;
	public static final int IMAGE_CURSOR=2;
	public static final int IMAGE_ENHMETAFILE=3;

	public static final int LR_DEFAULTCOLOR     =0x0000;
	public static final int LR_MONOCHROME       =0x0001;
	public static final int LR_COLOR            =0x0002;
	public static final int LR_COPYRETURNORG    =0x0004;
	public static final int LR_COPYDELETEORG    =0x0008;
	public static final int LR_LOADFROMFILE     =0x0010;
	public static final int LR_LOADTRANSPARENT  =0x0020;
	public static final int LR_DEFAULTSIZE      =0x0040;
	public static final int LR_VGACOLOR         =0x0080;
	public static final int LR_LOADMAP3DCOLORS  =0x1000;
	public static final int LR_CREATEDIBSECTION =0x2000;
	public static final int LR_COPYFROMRESOURCE =0x4000;
	public static final int LR_SHARED           =0x8000;
}
