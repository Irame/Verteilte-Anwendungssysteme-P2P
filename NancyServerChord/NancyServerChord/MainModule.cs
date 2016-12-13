using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using Nancy;
using Nancy.ModelBinding;
using System.IO;

namespace NancyServerChord
{
    public class MainModule : NancyModule
    {
        private object fileLock = new object();
        private string filename = "data.txt";

        private static List<EntryInfo> entrys = new List<EntryInfo>(); 

        public MainModule()
        {
            ReadFromFile();

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
                    AppendToFile(entryInfo);
                }

                return Response.AsRedirect("/");
            };
        }

        private void AppendToFile(EntryInfo ei)
        {
            string dataStr = $"{ei.Person};{ei.KeyName};{ei.DataType}";
            lock (fileLock)
            {
                File.AppendAllLines(filename, new[] { dataStr });
            }             
        }

        private void ReadFromFile()
        {
            foreach (var line in File.ReadAllLines(filename))
            {
                string[] split = line.Split(';');

                DataType type = DataType.File;
                Enum.TryParse(split[2], out type);

                var entry = new EntryInfo() { Person = split[0], KeyName = split[1], DataType = type };
                entry.Normalize();

                if (!entry.IsEmpty())
                {
                    entrys.Add(entry);
                }
            }
        }
    }
}
