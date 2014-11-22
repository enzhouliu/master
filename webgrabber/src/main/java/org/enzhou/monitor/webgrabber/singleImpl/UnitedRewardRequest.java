package org.enzhou.monitor.webgrabber.singleImpl;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PushbackInputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.enzhou.monitor.webgrabber.Grabber;
import org.enzhou.monitor.webgrabber.single.SingleRequest;

public class UnitedRewardRequest implements SingleRequest{

	private static final Logger log = LogManager.getLogger(Grabber.class.getSimpleName());
	private final String postContentHeader = "hdnServer=.67&hdnSID=E7C8B21E955B4DE496D2DE23C225F1CD&hdnLangCode=en-US&hdnPOS=US&hdnClient=100.33.109.108&hdnInactive=false&hdnAccountNumber=&hdnAccountNumberE=&hdnAccountStatus=&__EVENTTARGET=&__EVENTARGUMENT=&hdnTiming=0.9216626+seconds&__LASTFOCUS=&__VIEWSTATE=%2FwEPDwUKMTk1MTA4NjkxNQ9kFgJmD2QWAgIDDxYCHghvbnVubG9hZAUSUHVyY2hhc2VBYmFuZG9uKCk7FgICAQ8WAh4GYWN0aW9uBS1odHRwczovL3d3dy51bml0ZWQuY29tL3dlYi9lbi1VUy9kZWZhdWx0LmFzcHgWBAIFD2QWAgIJD2QWAgIDD2QWCgIBDxYCHglpbm5lcmh0bWxlZAIFDxYCHwIFlwM8YSBocmVmPSJodHRwOi8vY3J1aXNlcy51bml0ZWQuY29tLz9jbV9tbWM9TGluay1fLVVBTC1TaXRlX1JlZmVycmFsLV8tMjAxMjExMTktR2VuLUVuZy1GbHQtXy1HZW5lcmljX0hQJmludF9zb3VyY2U9dWFtZXImaW50X21lZGl1bT11YWNvbSZpbnRfY2FtcGFpZ249dW5pdGVkX2NydWlzZXMmaW50X2NvbnRlbnQ9ZW5nbGlzaCZwYXJ0bmVyX2NhdGVnb3J5PWFuY2lsbGFyeSZwYXJ0bmVyX25hbWU9dWNfd3RoX2ZsaWdodCZhc3NldF9wb3NpdGlvbj1obGMxJnRhcmdldGluZz11c19hbGwmbGF1bmNoX2RhdGU9MjAxMi0xMS0xOSI%2BPGltZyBzcmM9Ii93ZWIvZW4tVVMvaW1nL2hvbWVwYWdlL2NydWlzZTIwMDkwOC5naWYiIGFsdD0iQm9vayBVbml0ZWQgQ3J1aXNlcyIgaGVpZ2h0PSIzNSIgd2lkdGg9IjMwMCI%2BPC9hPmQCBw8WAh8CBZQLPHNjcmlwdCB0eXBlPSJ0ZXh0L2phdmFzY3JpcHQiPg0KPCEtLQ0KDQppZiAoImh0dHBzOiIgPT0gZG9jdW1lbnQubG9jYXRpb24ucHJvdG9jb2wpIHsNCiAgICBPQVNfdXJsID0gJ2h0dHBzOi8vb2FzYzE3LjI0N3JlYWxtZWRpYS5jb20nOw0KICAgIE9BU19zaXRlcGFnZSA9ICd3d3cudW5pdGVkLmNvbS9ob21lcGFnZSc7DQogICAgT0FTX3BvcyA9ICdCb3R0b21MZWZ0LEJvdHRvbVJpZ2h0IUJvdHRvbUxlZnQnOw0KICAgIE9BU19xdWVyeSA9ICcnOw0KDQogICAgaWYgKHR5cGVvZiBPQVNfUk5TID09ICd1bmRlZmluZWQnKXsNCiAgICAgICAgdmFyIE9BU19STiA9IG5ldyBTdHJpbmcgKE1hdGgucmFuZG9tKCkpOw0KICAgICAgICB2YXIgT0FTX1JOUyA9IE9BU19STi5zdWJzdHJpbmcoMiwgMTEpOw0KfQ0KICAgIC8qIEJlZ2luIE9BU19Cb3R0b21MZWZ0ICovZG9jdW1lbnQud3JpdGUoJzxzY3InICsgJ2lwdCB0eXBlPSJ0ZXh0L2phdmFzY3JpcHQiIHNyYz0iJyArIE9BU191cmwgKyAnL1JlYWxNZWRpYS9hZHMvYWRzdHJlYW1fanguYWRzLycgKyBPQVNfc2l0ZXBhZ2UgKyAnLzEnICsgT0FTX1JOUyArICdAJyArIE9BU19wb3MgKyAnPycgKyBPQVNfcXVlcnkgKyAnIj48L3NjcicgKyAnaXB0PicpOy8qIEVuZCBPQVNfQm90dG9tTGVmdCAqLw0KfSBlbHNlIHsgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgDQogICAgT0FTX3VybCA9ICdodHRwOi8vb2FzYzE3LjI0N3JlYWxtZWRpYS5jb20nOw0KICAgIE9BU19zaXRlcGFnZSA9ICd3d3cudW5pdGVkLmNvbS9ob21lcGFnZSc7DQogICAgT0FTX3BvcyA9ICdCb3R0b21MZWZ0LEJvdHRvbVJpZ2h0IUJvdHRvbUxlZnQnOw0KICAgIE9BU19xdWVyeSA9ICcnOw0KDQogICAgaWYgKHR5cGVvZiBPQVNfUk5TID09ICd1bmRlZmluZWQnKXsNCiAgICAgICAgdmFyIE9BU19STiA9IG5ldyBTdHJpbmcgKE1hdGgucmFuZG9tKCkpOw0KICAgICAgICB2YXIgT0FTX1JOUyA9IE9BU19STi5zdWJzdHJpbmcoMiwgMTEpOw0KfQ0KICAgIGRvY3VtZW50LndyaXRlKCc8c2NyJyArICdpcHQgdHlwZT0idGV4dC9qYXZhc2NyaXB0IiBzcmM9IicgKyBPQVNfdXJsICsgJy9SZWFsTWVkaWEvYWRzL2Fkc3RyZWFtX2p4LmFkcy8nICsgT0FTX3NpdGVwYWdlICsgJy8xJyArIE9BU19STlMgKyAnQCcgKyBPQVNfcG9zICsgJz8nICsgT0FTX3F1ZXJ5ICsgJyI%2BPC9zY3InICsgJ2lwdD4nKTsNCn0NCi8vIC0tPg0KPC9zY3JpcHQ%2BZAIJDxYCHgRUZXh0BeINc3RyQmFubmVyU3JjQXR0cmlidXRlcz0lMmZ3ZWIlMmZmb3JtYXQlMmZpbWclMmZ0Z3QlMmZoYmFuMiUyZjIwMTIwNzE5X05ld1dpbmRvd0Jvb2wuc3dmJTNmYmclM2QlMmZ3ZWIlMmZlbi1VUyUyZmltZyUyZnRndCUyZmhiYW4yJTJmMjAxNDExMDNfcWMxNDA1MF9NUEhlcnR6X05vdjIwMTRfNTcyeDEzMC5qcGclMjZzdHJCYXNlVVJMJTNkaHR0cCUyNTNBJTI1MkYlMjUyRnd3dy51bml0ZWQuY29tJTI1MkZoZXJ0eiUyNTNGaW50X3NvdXJjZSUyNTNEbG95YWx0eSUyNTI2bW9kZSUyNTNEZCUyNTI2aW50X21lZGl1bSUyNTNEdWFjb20lMjUyNmludF9jYW1wYWlnbiUyNTNEcTQxNG1waGVydHolMjUyNmFsbSUyNTNEMTQwNTAlMjUyNnBhcnRuZXJfY2F0ZWdvcnklMjUzRGNhcnMlMjUyNnBhcnRuZXJfbmFtZSUyNTNEaGVydHolMjUyNmFzc2V0X3R5cGUlMjUzRGJhbm5lciUyNTI2YXNzZXRfcG9zaXRpb24lMjUzRGhiYW4yJTI1MjZsYXVuY2hfZGF0ZSUyNTNEMjAxNC0xMS0wMyUzZiUyNmJOZXdXaW5kb3clM2R0cnVlJTI2aW50X3NvdXJjZSUzZCUyNmludF9tZWRpdW0lM2QlMjZpbnRfY2FtcGFpZ24lM2QlMjZpbnRfY29udGVudCUzZGh0dHAlMjUzQSUyNTJGJTI1MkZ3d3cudW5pdGVkLmNvbSUyNTJGaGVydHolMjUzRmludF9zb3VyY2UlMjUzRGxveWFsdHklMjUyNm1vZGUlMjUzRGQlMjUyNmludF9tZWRpdW0lMjUzRHVhY29tJTI1MjZpbnRfY2FtcGFpZ24lMjUzRHE0MTRtcGhlcnR6JTI1MjZhbG0lMjUzRDE0MDUwJTI1MjZwYXJ0bmVyX2NhdGVnb3J5JTI1M0RjYXJzJTI1MjZwYXJ0bmVyX25hbWUlMjUzRGhlcnR6JTI1MjZhc3NldF90eXBlJTI1M0RiYW5uZXIlMjUyNmFzc2V0X3Bvc2l0aW9uJTI1M0RoYmFuMiUyNTI2bGF1bmNoX2RhdGUlMjUzRDIwMTQtMTEtMDMlMjZwYXJ0bmVyX25hbWUlM2QlMjZwcm9tb19jb2RlJTNkJTI2YXNzZXRfcG9zaXRpb24lM2RIQkFOMiUyNmxhdW5jaF9kYXRlJTNkMjAxNC0xMS0wMyZzdHJCYW5uZXJIcmVmQXR0cmlidXRlcz1odHRwJTI1M0ElMjUyRiUyNTJGd3d3LnVuaXRlZC5jb20lMjUyRmhlcnR6JTI1M0ZpbnRfc291cmNlJTI1M0Rsb3lhbHR5JTI1MjZtb2RlJTI1M0RkJTI1MjZpbnRfbWVkaXVtJTI1M0R1YWNvbSUyNTI2aW50X2NhbXBhaWduJTI1M0RxNDE0bXBoZXJ0eiUyNTI2YWxtJTI1M0QxNDA1MCUyNTI2cGFydG5lcl9jYXRlZ29yeSUyNTNEY2FycyUyNTI2cGFydG5lcl9uYW1lJTI1M0RoZXJ0eiUyNTI2YXNzZXRfdHlwZSUyNTNEYmFubmVyJTI1MjZhc3NldF9wb3NpdGlvbiUyNTNEaGJhbjIlMjUyNmxhdW5jaF9kYXRlJTI1M0QyMDE0LTExLTAzJTNmaW50X3NvdXJjZSUzZCUyNmludF9tZWRpdW0lM2QlMjZpbnRfY2FtcGFpZ24lM2QlMjZpbnRfY29udGVudCUzZGh0dHAlMjUzQSUyNTJGJTI1MkZ3d3cudW5pdGVkLmNvbSUyNTJGaGVydHolMjUzRmludF9zb3VyY2UlMjUzRGxveWFsdHklMjUyNm1vZGUlMjUzRGQlMjUyNmludF9tZWRpdW0lMjUzRHVhY29tJTI1MjZpbnRfY2FtcGFpZ24lMjUzRHE0MTRtcGhlcnR6JTI1MjZhbG0lMjUzRDE0MDUwJTI1MjZwYXJ0bmVyX2NhdGVnb3J5JTI1M0RjYXJzJTI1MjZwYXJ0bmVyX25hbWUlMjUzRGhlcnR6JTI1MjZhc3NldF90eXBlJTI1M0RiYW5uZXIlMjUyNmFzc2V0X3Bvc2l0aW9uJTI1M0RoYmFuMiUyNTI2bGF1bmNoX2RhdGUlMjUzRDIwMTQtMTEtMDMlMjZwYXJ0bmVyX25hbWUlM2QlMjZwcm9tb19jb2RlJTNkJTI2YXNzZXRfcG9zaXRpb24lM2RIQkFOMiUyNmxhdW5jaF9kYXRlJTNkMjAxNC0xMS0wM2QCDw9kFgYCAQ8PFgIeC05hdmlnYXRlVXJsBSkvdHJhdmVsL2NoZWNraW4vc3RhcnQuYXNweD9MYW5nQ29kZT1lbi1VU2RkAgUPZBYEAgMPDxYCHg1PbkNsaWVudENsaWNrBUhkb2N1bWVudC5mb3Jtc1swXS5hY3Rpb249J2h0dHA6Ly93d3cudW5pdGVkLmNvbS93ZWIvZW4tVVMvZGVmYXVsdC5hc3B4JztkZAIFDw8WAh8EBSkvdHJhdmVsL2NoZWNraW4vc3RhcnQuYXNweD9MYW5nQ29kZT1lbi1VU2RkAgcPZBYKAgEPFgIeB1Zpc2libGVnFgJmDw8WAh8GaGQWAgIGDxBkDxYBZhYBEAUGVW5pdGVkBQJVQWcWAWZkAgUPEGQPFgRmAgECAgIDFgQPBQ1UdWUuLCBOb3YuIDE4BQoxMS8xOC8yMDE0DwUNV2VkLiwgTm92LiAxOQUKMTEvMTkvMjAxNA8FDVRodS4sIE5vdi4gMjAFCjExLzIwLzIwMTQPBQ1GcmkuLCBOb3YuIDIxBQoxMS8yMS8yMDE0ZGQCBw9kFggCCA9kFgICAQ8WAh8CBZQBQ2l0eSBvciA8YSBocmVmPSJqYXZhc2NyaXB0Ok9wZW5BaXJwb3J0cyh0aGlzLCdjdGwwMF9Db250ZW50SW5mb19DaGVja2luZmxpZ2h0c3RhdHVzX09yaWdpbl90eHRPcmlnaW4nLCdPcmlnaW5BaXJwb3J0Jyk7IiB0YWJpbmRleD0iLTEiPmFpcnBvcnQ8L2E%2BOmQCDA9kFgICAg8WDB4NRW5hYmxlQ2FjaGluZ2ceDVVzZUNvbnRleHRLZXlnHhJDb21wbGV0aW9uU2V0Q291bnQC3AseEkNvbXBsZXRpb25JbnRlcnZhbAL6AR4KQ29udGV4dEtleQUFZW4tVVMeB0VuYWJsZWRnZAIODxAPFgIeC18hRGF0YUJvdW5kZxYCHghvbmNoYW5nZQVlamF2YXNjcmlwdDpkb2N1bWVudC5mb3Jtc1swXS5jdGwwMF9Db250ZW50SW5mb19DaGVja2luZmxpZ2h0c3RhdHVzX09yaWdpbl90eHRPcmlnaW4udmFsdWU9dGhpcy52YWx1ZTtkFgBkAhAPFgQeDVdhdGVybWFya1RleHQFBEZyb20fDGdkAgkPZBYKAgcPZBYCAgEPFgIfAgWjAUNpdHkgb3IgPGEgaHJlZj0iamF2YXNjcmlwdDpPcGVuQWlycG9ydHModGhpcywnY3RsMDBfQ29udGVudEluZm9fQ2hlY2tpbmZsaWdodHN0YXR1c19EZXN0aW5hdGlvbl90eHREZXN0aW5hdGlvbicsJ0Rlc3RpbmF0aW9uQWlycG9ydCcpOyIgdGFiaW5kZXg9Ii0xIj5haXJwb3J0PC9hPjpkAgkPEA8WAh8NZ2RkFgBkAg0PZBYCAgIPFgwfB2cfCGcfCQLcCx8KAvoBHwsFBWVuLVVTHwxnZAIPDxAPFgIfDWcWAh8OBW9qYXZhc2NyaXB0OmRvY3VtZW50LmZvcm1zWzBdLmN0bDAwX0NvbnRlbnRJbmZvX0NoZWNraW5mbGlnaHRzdGF0dXNfRGVzdGluYXRpb25fdHh0RGVzdGluYXRpb24udmFsdWU9dGhpcy52YWx1ZTtkFgBkAhEPFgQfDwUCVG8fDGdkAgsPDxYCHwUFSGRvY3VtZW50LmZvcm1zWzBdLmFjdGlvbj0naHR0cDovL3d3dy51bml0ZWQuY29tL3dlYi9lbi1VUy9kZWZhdWx0LmFzcHgnO2RkAgsPDxYCHwZoZGQYAQUeX19Db250cm9sc1JlcXVpcmVQb3N0QmFja0tleV9fFhYFGGN0bDAwJEN1c3RvbWVySGVhZGVyJHJkMQUYY3RsMDAkQ3VzdG9tZXJIZWFkZXIkcmQyBRhjdGwwMCRDdXN0b21lckhlYWRlciRyZDMFHGN0bDAwJEN1c3RvbWVySGVhZGVyJGNoa1NhdmUFKWN0bDAwJENvbnRlbnRJbmZvJEJvb2tpbmcxJHJkb1NlYXJjaFR5cGUxBSljdGwwMCRDb250ZW50SW5mbyRCb29raW5nMSRyZG9TZWFyY2hUeXBlMgUpY3RsMDAkQ29udGVudEluZm8kQm9va2luZzEkcmRvU2VhcmNoVHlwZTIFLmN0bDAwJENvbnRlbnRJbmZvJEJvb2tpbmcxJE5lYXJieWFpciRjaGtGbHRPcHQFLGN0bDAwJENvbnRlbnRJbmZvJEJvb2tpbmcxJEFsdERhdGUkY2hrRmx0T3B0BTZjdGwwMCRDb250ZW50SW5mbyRCb29raW5nMSREZXBEYXRlVGltZSRyZG9EYXRlU3BlY2lmaWMFMmN0bDAwJENvbnRlbnRJbmZvJEJvb2tpbmcxJERlcERhdGVUaW1lJHJkb0RhdGVGbGV4BTJjdGwwMCRDb250ZW50SW5mbyRCb29raW5nMSREZXBEYXRlVGltZSRyZG9EYXRlRmxleAUwY3RsMDAkQ29udGVudEluZm8kQm9va2luZzEkU2VhcmNoQnkkcmRvc2VhcmNoYnkxBTBjdGwwMCRDb250ZW50SW5mbyRCb29raW5nMSRTZWFyY2hCeSRyZG9zZWFyY2hieTIFMGN0bDAwJENvbnRlbnRJbmZvJEJvb2tpbmcxJFNlYXJjaEJ5JHJkb3NlYXJjaGJ5MgUwY3RsMDAkQ29udGVudEluZm8kQm9va2luZzEkU2VhcmNoQnkkcmRvc2VhcmNoYnkzBTBjdGwwMCRDb250ZW50SW5mbyRCb29raW5nMSRTZWFyY2hCeSRyZG9zZWFyY2hieTMFK2N0bDAwJENvbnRlbnRJbmZvJEJvb2tpbmcxJERpcmVjdCRjaGtGbHRPcHQFJWN0bDAwJENvbnRlbnRJbmZvJG1hbmFnZXJlcyRyZG9GbGlnaHQFImN0bDAwJENvbnRlbnRJbmZvJG1hbmFnZXJlcyRyZG9DYXIFImN0bDAwJENvbnRlbnRJbmZvJG1hbmFnZXJlcyRyZG9DYXIFNGN0bDAwJENvbnRlbnRJbmZvJGFjY291bnRzdW1tYXJ5JHJlbWVtYmVybWUkY2hrUmVtTWW55jYd0xrOl2z%2FdwQ%2BMX88SNbPgg%3D%3D&ctl00%24CustomerHeader%24ddlCountries=US&ctl00%24CustomerHeader%24rdlang=en-us&ctl00%24CustomerHeader%24chkSave=on&ctl00%24CustomerHeader%24countryText=&ctl00%24CustomerHeader%24langText=&ctl00%24CustomerHeader%24txtSiteSearch=Type+in+keyword&ctl00%24ContentInfo%24Booking1%24hdnText=Award+Travel&ctl00%24ContentInfo%24Booking1%24SearchType=rdoSearchType2&ctl00%24ContentInfo%24Booking1%24Origin%24txtOrigin=Beijing%2C+People%27s+Republic+of+China+%28PEK+-+Capital%29&ctl00%24ContentInfo%24Booking1%24Origin%24TBWE01_ClientState=&ctl00%24ContentInfo%24Booking1%24Destination%24txtDestination=New+York%2C+NY+%28NYC+-+All+Airports%29&ctl00%24ContentInfo%24Booking1%24Destination%24TBWE01_ClientState=&ctl00%24ContentInfo%24Booking1%24DepDateTime%24DateFlex=rdoDateSpecific&ctl00%24ContentInfo%24Booking1%24DepDateTime%24Depdate%24txtDptDate=1%2F29%2F2015&ctl00%24ContentInfo%24Booking1%24DepDateTime%24Deptime%24cboDptTime=+&ctl00%24ContentInfo%24Booking1%24DepDateTime%24MonthList1%24cboMonth=1%2F1%2F2015&ctl00%24ContentInfo%24Booking1%24DepDateTime%24LengthOfStay%24cboLengthOfStay=6&ctl00%24ContentInfo%24Booking1%24RetDateTime%24Retdate%24txtRetDate=12%2F1%2F2014&ctl00%24ContentInfo%24Booking1%24RetDateTime%24Rettime%24cboDptTime=+&ctl00%24ContentInfo%24Booking1%24Adult%24cboAdult=1&ctl00%24ContentInfo%24Booking1%24Offercode%24txtPromoCode=&ctl00%24ContentInfo%24Booking1%24Cabins%24cboCabin=Coach&ctl00%24ContentInfo%24Booking1%24SearchBy%24SearchBy=rdosearchby3&ctl00%24ContentInfo%24Booking1%24Direct%24chkFltOpt=on&ctl00%24ContentInfo%24Booking1%24btnSearchFlight=Search&ctl00%24ContentInfo%24Booking1%24Pckuploc%24txtOrigin=&ctl00%24ContentInfo%24Booking1%24Returnlocdropoff%24txtDestination=Same+as+pick-up&ctl00%24ContentInfo%24Booking1%24Pickupdate%24txtDptDate=mm%2Fdd%2Fyyyy&ctl00%24ContentInfo%24Booking1%24Pickuptime%24cboPickUpTime=10%3A00AM&ctl00%24ContentInfo%24Booking1%24Dropoffdate%24txtRetDate=mm%2Fdd%2Fyyyy&ctl00%24ContentInfo%24Booking1%24Dropofftime%24cboRetTime=10%3A00AM&ctl00%24ContentInfo%24Booking1%24Cartype%24cboCarType=5&ctl00%24ContentInfo%24Checkinflightstatus%24Onepassconfirm%24txtOPNum=&ctl00%24ContentInfo%24Checkinflightstatus%24FlightNumber%24txtFltNum=&ctl00%24ContentInfo%24Checkinflightstatus%24cboFltDates=11%2F19%2F2014&ctl00%24ContentInfo%24Checkinflightstatus%24Origin%24txtOrigin=&ctl00%24ContentInfo%24Checkinflightstatus%24Origin%24TBWE01_ClientState=&ctl00%24ContentInfo%24Checkinflightstatus%24Destination%24txtDestination=&ctl00%24ContentInfo%24Checkinflightstatus%24Destination%24TBWE01_ClientState=&ctl00%24ContentInfo%24manageres%24confirmationOptions=rdoFlight&ctl00%24ContentInfo%24manageres%24ConfNum%24txtPNR=&ctl00%24ContentInfo%24manageres%24LastName%24txtLName=&ctl00%24ContentInfo%24accountsummary%24OpNum1%24txtOPNum=&ctl00%24ContentInfo%24accountsummary%24OpPin1%24txtOPPin=&hiddenInputToUpdateATBuffer_CommonToolkitScripts=1" ;
	private final String host = "http://www.united.com";
	
	private StringBuffer appendParameter(StringBuffer sb, String key, String value){
		try {
			String encodedKey = URLEncoder.encode(key, "UTF-8");
			String encodedValue = URLEncoder.encode(value, "UTF-8");
			return sb.append("&").append(encodedKey).append("=").append(encodedValue);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return sb;
	}
	
	@Override
	public String sendPostRequest() {
		String url = "/web/en-US/default.aspx";
		HttpURLConnection connection = null;
		try {
			URL uaURL = new URL(host + url);
			connection = (HttpURLConnection)uaURL.openConnection();
			connection.setInstanceFollowRedirects(false);
			connection.setRequestMethod("POST");
			connection.setRequestProperty("User-Agent", " Mozilla/5.0 (Windows NT 6.1; WOW64; rv:33.0) Gecko/20100101 Firefox/33.0");
			connection.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
			connection.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
			connection.setRequestProperty("Accept-Encoding", "gzip, deflate");
			connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
			connection.setRequestProperty("Content-Length", Integer.toString(postContentHeader.getBytes().length));
			//connection.setRequestProperty("Cookie", cookie);
			connection.setRequestProperty("Connection", "keep-alive");
			
			connection.setUseCaches(false);
			connection.setDoInput(true);
			connection.setDoOutput(true);
			
			DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
			wr.writeBytes(postContentHeader);
			wr.flush();
			wr.close();
			
			if (connection.getResponseCode() == HttpURLConnection.HTTP_MOVED_TEMP   
		            || connection.getResponseCode() == HttpURLConnection.HTTP_MOVED_PERM){
				Map<String, List<String>> header = connection.getHeaderFields();
				for(String key: header.keySet())
					System.out.println(key + ":" + header.get(key));
				return sendRedirectRequest(connection);
				
			}
			
			InputStream is = connection.getInputStream();
			BufferedReader reader = new BufferedReader(new InputStreamReader(is));
			String line;
			StringBuffer response = new StringBuffer();
			while((line = reader.readLine()) != null){
				response.append(line).append("\r");
			}
			reader.close();
			return response.toString();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";
	}
	
	private String sendRedirectRequest(HttpURLConnection conn) {
		// TODO Auto-generated method stub
		try {
			URL redirectURL = new URL(host + conn.getHeaderField("Location"));
		
			HttpURLConnection connection = (HttpURLConnection)redirectURL.openConnection();
			connection.setInstanceFollowRedirects(false);
			connection.setRequestMethod("GET");
			connection.setRequestProperty("User-Agent", " Mozilla/5.0 (Windows NT 6.1; WOW64; rv:33.0) Gecko/20100101 Firefox/33.0");
			connection.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
			connection.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
			connection.setRequestProperty("Accept-Encoding", "gzip, deflate");
			connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
			//connection.setRequestProperty("Content-Length", Integer.toString(postContentHeader.getBytes().length));
			List<String> cookies = conn.getHeaderFields().get("Set-Cookie");
			StringBuffer cookieBuffer = new StringBuffer();
			for(String cookie: cookies){
				cookieBuffer.append(cookie.split(";")[0]).append(";");
			}
			String cookieString = cookieBuffer.toString();
			
			connection.setRequestProperty("Cookie", cookieString.substring(0, cookieString.length()-1));
			connection.setRequestProperty("Connection", "keep-alive");
			
			if (connection.getResponseCode() == HttpURLConnection.HTTP_MOVED_TEMP   
		            || connection.getResponseCode() == HttpURLConnection.HTTP_MOVED_PERM){
				Map<String, List<String>> header = connection.getHeaderFields();
				for(String key: header.keySet())
					System.out.println(key + ":" + header.get(key));
				return sendRedirectRequest(connection);
				
			}
			InputStream is = connection.getInputStream();
			if(connection.getHeaderField("Content-Encoding").equalsIgnoreCase("gzip")){
				is = decompressStream(connection.getInputStream());
			}
			
			BufferedReader reader = new BufferedReader(new InputStreamReader(is));
			String line;
			StringBuffer response = new StringBuffer();
			while((line = reader.readLine()) != null){
				response.append(line).append("\r");
			}
			reader.close();
			return response.toString();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";
	}
	
	private InputStream decompressStream(InputStream input) throws IOException {
	     PushbackInputStream pb = new PushbackInputStream( input, 2 ); //we need a pushbackstream to look ahead
	     byte [] signature = new byte[2];
	     pb.read( signature ); //read the signature
	     pb.unread( signature ); //push back the signature to the stream
	     if( signature[ 0 ] == (byte) 0x1f && signature[ 1 ] == (byte) 0x8b ) //check if matches standard gzip magic number
	       return new GZIPInputStream( pb );
	     else 
	       return pb;
	}

	@Override
	public String sendGetRequest() {
		// TODO Auto-generated method stub
		return null;
	}

}
