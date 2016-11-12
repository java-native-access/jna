/*
 * The contents of this file is dual-licensed under 2 
 * alternative Open Source/Free licenses: LGPL 2.1 or later and 
 * Apache License 2.0. (starting with JNA version 4.0.0).
 * 
 * You can freely decide which license you want to apply to 
 * the project.
 * 
 * You may obtain a copy of the LGPL License at:
 * 
 * http://www.gnu.org/licenses/licenses.html
 * 
 * A copy is also included in the downloadable source code package
 * containing JNA, in file "LGPL2.1".
 * 
 * You may obtain a copy of the Apache License at:
 * 
 * http://www.apache.org/licenses/
 * 
 * A copy is also included in the downloadable source code package
 * containing JNA, in file "AL2.0".
 */
package com.sun.jna.platform.win32;

import com.sun.jna.platform.win32.Guid.GUID;

/**
 * Ported from KnownFolders.h.
 * Microsoft Windows SDK 7.0A.
 * @author Martin Steiger
 */
public class KnownFolders
{
/**
 * <ul>
 * <li>display name: "Network"</li>
 * <li>legacy display name: "My Network Places"</li>
 * <li>default path: </li>
 * <li>legacy CSIDL value: CSIDL_NETWORK</li>
 * </ul>
 */
public static final GUID FOLDERID_NetworkFolder  =         GUID.fromString("{D20BEEC4-5CA8-4905-AE3B-BF251EA09B53}");

//{0AC0837C-BBF8-452A-850D-79D08E667CA7}
public static final GUID FOLDERID_ComputerFolder =         GUID.fromString("{0AC0837C-BBF8-452A-850D-79D08E667CA7}");

//{4D9F7874-4E0C-4904-967B-40B0D20C3E4B}
public static final GUID FOLDERID_InternetFolder =         GUID.fromString("{4D9F7874-4E0C-4904-967B-40B0D20C3E4B}");

//{82A74AEB-AEB4-465C-A014-D097EE346D63}
public static final GUID FOLDERID_ControlPanelFolder =     GUID.fromString("{82A74AEB-AEB4-465C-A014-D097EE346D63}");

//{76FC4E2D-D6AD-4519-A663-37BD56068185}
public static final GUID FOLDERID_PrintersFolder =         GUID.fromString("{76FC4E2D-D6AD-4519-A663-37BD56068185}");

//{43668BF8-C14E-49B2-97C9-747784D784B7}
public static final GUID FOLDERID_SyncManagerFolder =      GUID.fromString("{43668BF8-C14E-49B2-97C9-747784D784B7}");

//{0F214138-B1D3-4a90-BBA9-27CBC0C5389A}
public static final GUID FOLDERID_SyncSetupFolder =        GUID.fromString("{0f214138-b1d3-4a90-bba9-27cbc0c5389a}");

//{4bfefb45-347d-4006-a5be-ac0cb0567192}
public static final GUID FOLDERID_ConflictFolder =         GUID.fromString("{4bfefb45-347d-4006-a5be-ac0cb0567192}");

//{289a9a43-be44-4057-a41b-587a76d7e7f9}
public static final GUID FOLDERID_SyncResultsFolder =      GUID.fromString("{289a9a43-be44-4057-a41b-587a76d7e7f9}");

//{B7534046-3ECB-4C18-BE4E-64CD4CB7D6AC}
public static final GUID FOLDERID_RecycleBinFolder =       GUID.fromString("{B7534046-3ECB-4C18-BE4E-64CD4CB7D6AC}");

//{6F0CD92B-2E97-45D1-88FF-B0D186B8DEDD}
public static final GUID FOLDERID_ConnectionsFolder =      GUID.fromString("{6F0CD92B-2E97-45D1-88FF-B0D186B8DEDD}");

//{FD228CB7-AE11-4AE3-864C-16F3910AB8FE}
public static final GUID FOLDERID_Fonts =                  GUID.fromString("{FD228CB7-AE11-4AE3-864C-16F3910AB8FE}");

/**
 * <ul>
 * <li>display name:        "Desktop"</li>
 * <li>default path:        "C:\Users\&lt;UserName&gt;\Desktop"</li>
 * <li>legacy default path: "C:\Documents and Settings\&lt;userName&gt;\Desktop"</li>
 * <li>legacy CSIDL value:  CSIDL_DESKTOP</li>
 * </ul>
 */
//{B4BFCC3A-DB2C-424C-B029-7FE99A87C641}
public static final GUID FOLDERID_Desktop =                GUID.fromString("{B4BFCC3A-DB2C-424C-B029-7FE99A87C641}");

//{B97D20BB-F46A-4C97-BA10-5E3608430854}
public static final GUID FOLDERID_Startup =                GUID.fromString("{B97D20BB-F46A-4C97-BA10-5E3608430854}");

//{A77F5D77-2E2B-44C3-A6A2-ABA601054A51}
public static final GUID FOLDERID_Programs =               GUID.fromString("{A77F5D77-2E2B-44C3-A6A2-ABA601054A51}");

//{625B53C3-AB48-4EC1-BA1F-A1EF4146FC19}
public static final GUID FOLDERID_StartMenu =              GUID.fromString("{625B53C3-AB48-4EC1-BA1F-A1EF4146FC19}");

//{AE50C081-EBD2-438A-8655-8A092E34987A}
public static final GUID FOLDERID_Recent =                 GUID.fromString("{AE50C081-EBD2-438A-8655-8A092E34987A}");

//{8983036C-27C0-404B-8F08-102D10DCFD74}
public static final GUID FOLDERID_SendTo =                 GUID.fromString("{8983036C-27C0-404B-8F08-102D10DCFD74}");

//{FDD39AD0-238F-46AF-ADB4-6C85480369C7}
public static final GUID FOLDERID_Documents =              GUID.fromString("{FDD39AD0-238F-46AF-ADB4-6C85480369C7}");

//{1777F761-68AD-4D8A-87BD-30B759FA33DD}
public static final GUID FOLDERID_Favorites =              GUID.fromString("{1777F761-68AD-4D8A-87BD-30B759FA33DD}");

//{C5ABBF53-E17F-4121-8900-86626FC2C973}
public static final GUID FOLDERID_NetHood =                GUID.fromString("{C5ABBF53-E17F-4121-8900-86626FC2C973}");

//{9274BD8D-CFD1-41C3-B35E-B13F55A758F4}
public static final GUID FOLDERID_PrintHood =              GUID.fromString("{9274BD8D-CFD1-41C3-B35E-B13F55A758F4}");

//{A63293E8-664E-48DB-A079-DF759E0509F7}
public static final GUID FOLDERID_Templates =              GUID.fromString("{A63293E8-664E-48DB-A079-DF759E0509F7}");

//{82A5EA35-D9CD-47C5-9629-E15D2F714E6E}
public static final GUID FOLDERID_CommonStartup =          GUID.fromString("{82A5EA35-D9CD-47C5-9629-E15D2F714E6E}");

//{0139D44E-6AFE-49F2-8690-3DAFCAE6FFB8}
public static final GUID FOLDERID_CommonPrograms =         GUID.fromString("{0139D44E-6AFE-49F2-8690-3DAFCAE6FFB8}");

//{A4115719-D62E-491D-AA7C-E74B8BE3B067}
public static final GUID FOLDERID_CommonStartMenu =        GUID.fromString("{A4115719-D62E-491D-AA7C-E74B8BE3B067}");

//{C4AA340D-F20F-4863-AFEF-F87EF2E6BA25}
public static final GUID FOLDERID_PublicDesktop =          GUID.fromString("{C4AA340D-F20F-4863-AFEF-F87EF2E6BA25}");

//{62AB5D82-FDC1-4DC3-A9DD-070D1D495D97}
public static final GUID FOLDERID_ProgramData =            GUID.fromString("{62AB5D82-FDC1-4DC3-A9DD-070D1D495D97}");

//{B94237E7-57AC-4347-9151-B08C6C32D1F7}
public static final GUID FOLDERID_CommonTemplates =        GUID.fromString("{B94237E7-57AC-4347-9151-B08C6C32D1F7}");

//{ED4824AF-DCE4-45A8-81E2-FC7965083634}
public static final GUID FOLDERID_PublicDocuments =        GUID.fromString("{ED4824AF-DCE4-45A8-81E2-FC7965083634}");

//{3EB685DB-65F9-4CF6-A03A-E3EF65729F3D}
public static final GUID FOLDERID_RoamingAppData =         GUID.fromString("{3EB685DB-65F9-4CF6-A03A-E3EF65729F3D}");

//{F1B32785-6FBA-4FCF-9D55-7B8E7F157091}
public static final GUID FOLDERID_LocalAppData =           GUID.fromString("{F1B32785-6FBA-4FCF-9D55-7B8E7F157091}");

//{A520A1A4-1780-4FF6-BD18-167343C5AF16}
public static final GUID FOLDERID_LocalAppDataLow =        GUID.fromString("{A520A1A4-1780-4FF6-BD18-167343C5AF16}");

//{352481E8-33BE-4251-BA85-6007CAEDCF9D}
public static final GUID FOLDERID_InternetCache =          GUID.fromString("{352481E8-33BE-4251-BA85-6007CAEDCF9D}");

//{2B0F765D-C0E9-4171-908E-08A611B84FF6}
public static final GUID FOLDERID_Cookies =                GUID.fromString("{2B0F765D-C0E9-4171-908E-08A611B84FF6}");

//{D9DC8A3B-B784-432E-A781-5A1130A75963}
public static final GUID FOLDERID_History =                GUID.fromString("{D9DC8A3B-B784-432E-A781-5A1130A75963}");

//{1AC14E77-02E7-4E5D-B744-2EB1AE5198B7}
public static final GUID FOLDERID_System =                 GUID.fromString("{1AC14E77-02E7-4E5D-B744-2EB1AE5198B7}");

//{D65231B0-B2F1-4857-A4CE-A8E7C6EA7D27}
public static final GUID FOLDERID_SystemX86 =              GUID.fromString("{D65231B0-B2F1-4857-A4CE-A8E7C6EA7D27}");

//{F38BF404-1D43-42F2-9305-67DE0B28FC23}
public static final GUID FOLDERID_Windows =                GUID.fromString("{F38BF404-1D43-42F2-9305-67DE0B28FC23}");

//{5E6C858F-0E22-4760-9AFE-EA3317B67173}
public static final GUID FOLDERID_Profile =                GUID.fromString("{5E6C858F-0E22-4760-9AFE-EA3317B67173}");

//{33E28130-4E1E-4676-835A-98395C3BC3BB}
public static final GUID FOLDERID_Pictures =               GUID.fromString("{33E28130-4E1E-4676-835A-98395C3BC3BB}");

//{7C5A40EF-A0FB-4BFC-874A-C0F2E0B9FA8E}
public static final GUID FOLDERID_ProgramFilesX86 =        GUID.fromString("{7C5A40EF-A0FB-4BFC-874A-C0F2E0B9FA8E}");

//{DE974D24-D9C6-4D3E-BF91-F4455120B917}
public static final GUID FOLDERID_ProgramFilesCommonX86 =  GUID.fromString("{DE974D24-D9C6-4D3E-BF91-F4455120B917}");

//{6D809377-6AF0-444b-8957-A3773F02200E}
public static final GUID FOLDERID_ProgramFilesX64 =        GUID.fromString("{6d809377-6af0-444b-8957-a3773f02200e}");

//{6365D5A7-0F0D-45e5-87F6-0DA56B6A4F7D}
public static final GUID FOLDERID_ProgramFilesCommonX64 =  GUID.fromString("{6365d5a7-0f0d-45e5-87f6-0da56b6a4f7d}");

//{905e63b6-c1bf-494e-b29c-65b732d3d21a}
public static final GUID FOLDERID_ProgramFiles =           GUID.fromString("{905e63b6-c1bf-494e-b29c-65b732d3d21a}");

//{F7F1ED05-9F6D-47A2-AAAE-29D317C6F066}
public static final GUID FOLDERID_ProgramFilesCommon =     GUID.fromString("{F7F1ED05-9F6D-47A2-AAAE-29D317C6F066}");

//{5cd7aee2-2219-4a67-b85d-6c9ce15660cb}
public static final GUID FOLDERID_UserProgramFiles =       GUID.fromString("{5cd7aee2-2219-4a67-b85d-6c9ce15660cb}");

//{bcbd3057-ca5c-4622-b42d-bc56db0ae516}
public static final GUID FOLDERID_UserProgramFilesCommon = GUID.fromString("{bcbd3057-ca5c-4622-b42d-bc56db0ae516}");

//{724EF170-A42D-4FEF-9F26-B60E846FBA4F}
public static final GUID FOLDERID_AdminTools =             GUID.fromString("{724EF170-A42D-4FEF-9F26-B60E846FBA4F}");

//{D0384E7D-BAC3-4797-8F14-CBA229B392B5}
public static final GUID FOLDERID_CommonAdminTools =       GUID.fromString("{D0384E7D-BAC3-4797-8F14-CBA229B392B5}");

//{4BD8D571-6D19-48D3-BE97-422220080E43}
public static final GUID FOLDERID_Music =                  GUID.fromString("{4BD8D571-6D19-48D3-BE97-422220080E43}");

//{18989B1D-99B5-455B-841C-AB7C74E4DDFC}
public static final GUID FOLDERID_Videos =                 GUID.fromString("{18989B1D-99B5-455B-841C-AB7C74E4DDFC}");

//{C870044B-F49E-4126-A9C3-B52A1FF411E8}
public static final GUID FOLDERID_Ringtones =              GUID.fromString("{C870044B-F49E-4126-A9C3-B52A1FF411E8}");

//{B6EBFB86-6907-413C-9AF7-4FC2ABF07CC5}
public static final GUID FOLDERID_PublicPictures =         GUID.fromString("{B6EBFB86-6907-413C-9AF7-4FC2ABF07CC5}");

//{3214FAB5-9757-4298-BB61-92A9DEAA44FF}
public static final GUID FOLDERID_PublicMusic =            GUID.fromString("{3214FAB5-9757-4298-BB61-92A9DEAA44FF}");

//{2400183A-6185-49FB-A2D8-4A392A602BA3}
public static final GUID FOLDERID_PublicVideos =           GUID.fromString("{2400183A-6185-49FB-A2D8-4A392A602BA3}");

//{E555AB60-153B-4D17-9F04-A5FE99FC15EC}
public static final GUID FOLDERID_PublicRingtones =        GUID.fromString("{E555AB60-153B-4D17-9F04-A5FE99FC15EC}");

//{8AD10C31-2ADB-4296-A8F7-E4701232C972}
public static final GUID FOLDERID_ResourceDir =            GUID.fromString("{8AD10C31-2ADB-4296-A8F7-E4701232C972}");

//{2A00375E-224C-49DE-B8D1-440DF7EF3DDC}
public static final GUID FOLDERID_LocalizedResourcesDir =  GUID.fromString("{2A00375E-224C-49DE-B8D1-440DF7EF3DDC}");

//{C1BAE2D0-10DF-4334-BEDD-7AA20B227A9D}
public static final GUID FOLDERID_CommonOEMLinks =         GUID.fromString("{C1BAE2D0-10DF-4334-BEDD-7AA20B227A9D}");

//{9E52AB10-F80D-49DF-ACB8-4330F5687855}
public static final GUID FOLDERID_CDBurning =              GUID.fromString("{9E52AB10-F80D-49DF-ACB8-4330F5687855}");

//{0762D272-C50A-4BB0-A382-697DCD729B80}
public static final GUID FOLDERID_UserProfiles =           GUID.fromString("{0762D272-C50A-4BB0-A382-697DCD729B80}");

//{DE92C1C7-837F-4F69-A3BB-86E631204A23}
public static final GUID FOLDERID_Playlists =              GUID.fromString("{DE92C1C7-837F-4F69-A3BB-86E631204A23}");

//{15CA69B3-30EE-49C1-ACE1-6B5EC372AFB5}
public static final GUID FOLDERID_SamplePlaylists =        GUID.fromString("{15CA69B3-30EE-49C1-ACE1-6B5EC372AFB5}");

//{B250C668-F57D-4EE1-A63C-290EE7D1AA1F}
public static final GUID FOLDERID_SampleMusic =            GUID.fromString("{B250C668-F57D-4EE1-A63C-290EE7D1AA1F}");

//{C4900540-2379-4C75-844B-64E6FAF8716B}
public static final GUID FOLDERID_SamplePictures =         GUID.fromString("{C4900540-2379-4C75-844B-64E6FAF8716B}");

//{859EAD94-2E85-48AD-A71A-0969CB56A6CD}
public static final GUID FOLDERID_SampleVideos =           GUID.fromString("{859EAD94-2E85-48AD-A71A-0969CB56A6CD}");

//{69D2CF90-FC33-4FB7-9A0C-EBB0F0FCB43C}
public static final GUID FOLDERID_PhotoAlbums =            GUID.fromString("{69D2CF90-FC33-4FB7-9A0C-EBB0F0FCB43C}");

//{DFDF76A2-C82A-4D63-906A-5644AC457385}
public static final GUID FOLDERID_Public =                 GUID.fromString("{DFDF76A2-C82A-4D63-906A-5644AC457385}");

//{df7266ac-9274-4867-8d55-3bd661de872d}
public static final GUID FOLDERID_ChangeRemovePrograms =   GUID.fromString("{df7266ac-9274-4867-8d55-3bd661de872d}");

//{a305ce99-f527-492b-8b1a-7e76fa98d6e4}
public static final GUID FOLDERID_AppUpdates =             GUID.fromString("{a305ce99-f527-492b-8b1a-7e76fa98d6e4}");

//{de61d971-5ebc-4f02-a3a9-6c82895e5c04}
public static final GUID FOLDERID_AddNewPrograms =         GUID.fromString("{de61d971-5ebc-4f02-a3a9-6c82895e5c04}");

//{374DE290-123F-4565-9164-39C4925E467B}
public static final GUID FOLDERID_Downloads =              GUID.fromString("{374de290-123f-4565-9164-39c4925e467b}");

//{3D644C9B-1FB8-4f30-9B45-F670235F79C0}
public static final GUID FOLDERID_PublicDownloads =        GUID.fromString("{3d644c9b-1fb8-4f30-9b45-f670235f79c0}");

//{7d1d3a04-debb-4115-95cf-2f29da2920da}
public static final GUID FOLDERID_SavedSearches =          GUID.fromString("{7d1d3a04-debb-4115-95cf-2f29da2920da}");

//{52a4f021-7b75-48a9-9f6b-4b87a210bc8f}
public static final GUID FOLDERID_QuickLaunch =            GUID.fromString("{52a4f021-7b75-48a9-9f6b-4b87a210bc8f}");

//{56784854-C6CB-462b-8169-88E350ACB882}
public static final GUID FOLDERID_Contacts =               GUID.fromString("{56784854-c6cb-462b-8169-88e350acb882}");

//{A75D362E-50FC-4fb7-AC2C-A8BEAA314493}
public static final GUID FOLDERID_SidebarParts =           GUID.fromString("{a75d362e-50fc-4fb7-ac2c-a8beaa314493}");

//{7B396E54-9EC5-4300-BE0A-2482EBAE1A26}
public static final GUID FOLDERID_SidebarDefaultParts =    GUID.fromString("{7b396e54-9ec5-4300-be0a-2482ebae1a26}");

//{DEBF2536-E1A8-4c59-B6A2-414586476AEA}
public static final GUID FOLDERID_PublicGameTasks =        GUID.fromString("{debf2536-e1a8-4c59-b6a2-414586476aea}");

//{054FAE61-4DD8-4787-80B6-090220C4B700}
public static final GUID FOLDERID_GameTasks =              GUID.fromString("{054fae61-4dd8-4787-80b6-090220c4b700}");

//{4C5C32FF-BB9D-43b0-B5B4-2D72E54EAAA4}
public static final GUID FOLDERID_SavedGames =             GUID.fromString("{4c5c32ff-bb9d-43b0-b5b4-2d72e54eaaa4}");

//{CAC52C1A-B53D-4edc-92D7-6B2E8AC19434}
public static final GUID FOLDERID_Games =                  GUID.fromString("{cac52c1a-b53d-4edc-92d7-6b2e8ac19434}");

//{98ec0e18-2098-4d44-8644-66979315a281}
public static final GUID FOLDERID_SEARCH_MAPI =            GUID.fromString("{98ec0e18-2098-4d44-8644-66979315a281}");

//{ee32e446-31ca-4aba-814f-a5ebd2fd6d5e}
public static final GUID FOLDERID_SEARCH_CSC =             GUID.fromString("{ee32e446-31ca-4aba-814f-a5ebd2fd6d5e}");

//{bfb9d5e0-c6a9-404c-b2b2-ae6db6af4968}
public static final GUID FOLDERID_Links =                  GUID.fromString("{bfb9d5e0-c6a9-404c-b2b2-ae6db6af4968}");

//{f3ce0f7c-4901-4acc-8648-d5d44b04ef8f}
public static final GUID FOLDERID_UsersFiles =             GUID.fromString("{f3ce0f7c-4901-4acc-8648-d5d44b04ef8f}");

//{A302545D-DEFF-464b-ABE8-61C8648D939B}
public static final GUID FOLDERID_UsersLibraries =         GUID.fromString("{a302545d-deff-464b-abe8-61c8648d939b}");

//{190337d1-b8ca-4121-a639-6d472d16972a}
public static final GUID FOLDERID_SearchHome =             GUID.fromString("{190337d1-b8ca-4121-a639-6d472d16972a}");

//{2C36C0AA-5812-4b87-BFD0-4CD0DFB19B39}
public static final GUID FOLDERID_OriginalImages =         GUID.fromString("{2C36C0AA-5812-4b87-bfd0-4cd0dfb19b39}");

//{7b0db17d-9cd2-4a93-9733-46cc89022e7c}
public static final GUID FOLDERID_DocumentsLibrary =       GUID.fromString("{7b0db17d-9cd2-4a93-9733-46cc89022e7c}");

//{2112AB0A-C86A-4ffe-A368-0DE96E47012E}
public static final GUID FOLDERID_MusicLibrary =           GUID.fromString("{2112ab0a-c86a-4ffe-a368-0de96e47012e}");

//{A990AE9F-A03B-4e80-94BC-9912D7504104}
public static final GUID FOLDERID_PicturesLibrary =        GUID.fromString("{a990ae9f-a03b-4e80-94bc-9912d7504104}");

//{491E922F-5643-4af4-A7EB-4E7A138D8174}
public static final GUID FOLDERID_VideosLibrary =          GUID.fromString("{491e922f-5643-4af4-a7eb-4e7a138d8174}");

//{1A6FDBA2-F42D-4358-A798-B74D745926C5}
public static final GUID FOLDERID_RecordedTVLibrary =      GUID.fromString("{1a6fdba2-f42d-4358-a798-b74d745926c5}");

//{52528A6B-B9E3-4add-B60D-588C2DBA842D}
public static final GUID FOLDERID_HomeGroup =              GUID.fromString("{52528a6b-b9e3-4add-b60d-588c2dba842d}");

//{5CE4A5E9-E4EB-479D-B89F-130C02886155}
public static final GUID FOLDERID_DeviceMetadataStore =    GUID.fromString("{5ce4a5e9-e4eb-479d-b89f-130c02886155}");

//{1B3EA5DC-B587-4786-B4EF-BD1DC332AEAE}
public static final GUID FOLDERID_Libraries =              GUID.fromString("{1b3ea5dc-b587-4786-b4ef-bd1dc332aeae}");

//{48daf80b-e6cf-4f4e-b800-0e69d84ee384}
public static final GUID FOLDERID_PublicLibraries =        GUID.fromString("{48daf80b-e6cf-4f4e-b800-0e69d84ee384}");

//{9e3995ab-1f9c-4f13-b827-48b24b6c7174}
public static final GUID FOLDERID_UserPinned =             GUID.fromString("{9e3995ab-1f9c-4f13-b827-48b24b6c7174}");

//{bcb5256f-79f6-4cee-b725-dc34e402fd46}
public static final GUID FOLDERID_ImplicitAppShortcuts =   GUID.fromString("{bcb5256f-79f6-4cee-b725-dc34e402fd46}");

}
