using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using Nancy.Hosting.Self;

namespace NancyServerChord
{
    class Program
    {
        static void Main(string[] args)
        {
            string baseURL = "http://localhost";
            string port = args.ElementAtOrDefault(0) ?? "80";

            Uri url = new Uri($"{baseURL}:{port}");

            using (var host = new NancyHost(url))
            {
                host.Start();   
                Console.WriteLine($"Running on {url.OriginalString}");
                Console.ReadLine();
            }
        }
    }
}
