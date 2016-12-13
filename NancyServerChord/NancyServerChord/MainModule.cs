using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using Nancy;
using Nancy.ModelBinding;

namespace NancyServerChord
{
    public class MainModule : NancyModule
    {
        private static List<EntryInfo> entrys = new List<EntryInfo>(); 

        public MainModule()
        {
            Get["/"] = _ =>
            {
                return View["index.html", entrys];
            };

            Post["/create"] = _ =>
            {
                EntryInfo entryInfo;
                entryInfo = this.Bind<EntryInfo>();

                entryInfo.Normalize();
                if (!entryInfo.IsEmpty())
                {
                    entrys.Add(entryInfo);
                }

                return Response.AsRedirect("/");
            };
        }
    }
}
